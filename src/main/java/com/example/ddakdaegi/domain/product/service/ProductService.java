package com.example.ddakdaegi.domain.product.service;

import com.example.ddakdaegi.domain.product.dto.request.ProductRequest;
import com.example.ddakdaegi.domain.product.dto.response.ProductResponse;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	/*
		상품 조회 메서드
	*/
	@Transactional(readOnly = true)
	public Page<ProductResponse> findAllProduct(int page, int size) {

		int adjustedPage = (page > 0) ? page - 1 : 0;
		//Pageable pageable = PageRequest.of(adjustedPage, size, Sort.by(updated_at).descending());
		Pageable pageable = PageRequest.of(adjustedPage, size);
		Page<Product> postPage = productRepository.findAll(pageable);

		List<ProductResponse> dtoList = postPage.getContent().stream()
			.map(ProductResponse::toDto)
			.toList();

		return new PageImpl<>(dtoList, pageable, postPage.getTotalElements());
	}


	/*
		상품 등록 메서드
	*/
	@Transactional
	public ProductResponse saveProduct(/*Long memberId,*/ ProductRequest productRequest) {

		//Member member = memberRepository.findMemberById(memberId);

		Product product = new Product(
			productRequest.getDescription(),
			productRequest.getName(),
			productRequest.getImage(),
			productRequest.getStock(),
			productRequest.getPrice()
		);

		productRepository.save(product);

		return new ProductResponse(product);
	}


}
