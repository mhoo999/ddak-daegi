package com.example.ddakdaegi.domain.product.service;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.image.repository.ImageRepository;
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
	private final ImageRepository imageRepository;

	/*
		상품 조회 메서드
	*/
	@Transactional(readOnly = true)
	public Page<ProductResponse> findAllProduct(Pageable pageable) {

		Page<Product> productPage = productRepository.findByDeletedAtNull(pageable);

		List<ProductResponse> dtoList = productPage.getContent().stream()
			.map(ProductResponse::toDto)
			.toList();

		return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
	}


	/*
		상품 등록 메서드
	*/
	@Transactional
	public ProductResponse saveProduct(AuthUser authUser, ProductRequest productRequest) {

		Member member = Member.fromAuthUser(authUser);

		// 등록할 이미지
		Image image = imageRepository.findById(productRequest.getImage())
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_IMAGE));

		Product product = new Product(
			member,
			productRequest.getDescription(),
			productRequest.getName(),
			image,
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


	/*
		상품 판매상태 변경 메서드
	*/
	@Transactional
	public ProductResponse setSoldOut(AuthUser authUser, Long productId, boolean soldOut){

		Member member = Member.fromAuthUser(authUser);

		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_PRODUCT));


		// 다른 유저의 상품 수정을 방지하는 기능
		if(product.getMember().getId() != member.getId()){
			throw new BaseException(ErrorCode.IS_NOT_YOUR_PRODUCT);
		}

		// Product 레코드 soldOut 의 저장된 값이 요청한 값과 동일한 경우 -> 불필요한 Repository.save 매서드 호출 방지
		if(product.getSoldOut() == soldOut){
			throw new BaseException(ErrorCode.SOLD_OUT_SAME_FLAG);
		}

		product.setSoldOut(soldOut);
		productRepository.save(product);

		return new ProductResponse(product);

	}


	/*
		상품 정보 수정 메서드
	*/
	@Transactional
	public ProductResponse editProduct(AuthUser authUser, Long productId, ProductRequest productRequest){

		Member member = Member.fromAuthUser(authUser);

		// 요청한 productId 값의 상품이 없을 경우
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_PRODUCT));

		// 다른 유저의 상품 수정을 방지하는 기능
		if(product.getMember().getId() != member.getId()){
			throw new BaseException(ErrorCode.IS_NOT_YOUR_PRODUCT);
		}

		// 수정 이미지
		Image image = imageRepository.findById(productRequest.getImage())
			.orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_IMAGE));

		// 상품 수정
		product.setDescription(productRequest.getDescription());
		product.setName(productRequest.getName());
		product.setImage(image);
		product.setStock(productRequest.getStock());
		product.setPrice(productRequest.getPrice());

		productRepository.save(product);

		return new ProductResponse(product);

	}



}
