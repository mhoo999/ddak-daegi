package com.example.ddakdaegi.domain.product.service;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.product.dto.request.ProductRequest;
import com.example.ddakdaegi.domain.product.dto.response.ProductResponse;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.product.repository.ProductRepository;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
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
	public Page<ProductResponse> findAllProduct(Pageable pageable) {

		Page<Product> postPage = productRepository.findByDeletedAtNull(pageable);

		List<ProductResponse> dtoList = postPage.getContent().stream()
			.map(ProductResponse::toDto)
			.toList();

		return new PageImpl<>(dtoList, pageable, postPage.getTotalElements());
	}


	/*
		상품 등록 메서드
	*/
	@Transactional
	public ProductResponse saveProduct(AuthUser authUser, ProductRequest productRequest) {

		Member member = Member.fromAuthUser(authUser);

		Product product = new Product(
			member,
			productRequest.getDescription(),
			productRequest.getName(),
			productRequest.getImage(),
			productRequest.getStock(),
			productRequest.getPrice()
		);

		productRepository.save(product);

		return new ProductResponse(product);
	}


	/*
		상품 단건 조회 메서드
	*/
	@Transactional
	public ProductResponse findProductById(Long id) {

		Product product = productRepository.findById(id)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_PRODUCT));

		return new ProductResponse(product);

	}

	/*
		상품 소프트딜리트 메서드
	*/
	@Transactional
	public ProductResponse softDeleteProduct(Long id) {

		Product product = productRepository.findById(id)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_PRODUCT));

		product.setDeletedAt(LocalDateTime.now()); // 소프트 딜리트 변수에 현재시간 대입 -> null 이 아니므로 더이상 DB에 레코드가 논리적으로 존재하지 않음

		productRepository.save(product);

		return new ProductResponse(product);
	}

}
