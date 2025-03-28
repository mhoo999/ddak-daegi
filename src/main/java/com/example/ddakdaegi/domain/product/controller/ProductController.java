package com.example.ddakdaegi.domain.product.controller;

import com.example.ddakdaegi.domain.product.dto.request.ProductRequest;
import com.example.ddakdaegi.domain.product.dto.request.ProductSoldOutSetRequest;
import com.example.ddakdaegi.domain.product.dto.response.ProductResponse;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.product.service.ProductService;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;


	/*
		Product(상품) 등록 메서드
	*/
	@PostMapping("/v1/products")
	public Response<ProductResponse> saveProduct(
		@AuthenticationPrincipal AuthUser authUser,
		@RequestBody ProductRequest productRequest
	) {
		ProductResponse productResponse = productService.saveProduct(authUser, productRequest);

		return Response.of(productResponse);
	}


	/*
		Product(상품) 다건 조회 메서드
	*/
	@GetMapping("/v1/products")
	public Response<Page<ProductResponse>> findAllProduct(
		@PageableDefault(page = 0, size = 5) Pageable pageable // 기본 page, size 크기 설정 파라미터
	) {
		Page<ProductResponse> productResponseDtoPage = productService.findAllProduct(pageable);

		return Response.of(productResponseDtoPage);
	}


	/*
		상품 삭제 소프트딜리트 메서드
	*/
	@DeleteMapping("/v1/products/delete/{productId}")
	public Response<ProductResponse> softDeleteProduct(@PathVariable("productId") Long productId) {

		ProductResponse productResponse = productService.softDeleteProduct(productId);

		return Response.of(productResponse);
	}


	/*
		상품 단건 조회
	*/
	@GetMapping("/v1/products/{productId}")
	public Response<ProductResponse> findProduct(@PathVariable("productId") Long productId) {

		ProductResponse productResponse = productService.findProductById(productId);

		return Response.of(productResponse);
	}


	/*
		상품 판매상태 변경
	*/
	@PatchMapping("/v1/products/soldout/{productId}")
	public Response<ProductResponse> setSoldOut(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable("productId") Long productId,
		@RequestBody ProductSoldOutSetRequest productSoldOutSetRequest
	) {
		ProductResponse productResponse = productService.setSoldOut(authUser, productId, productSoldOutSetRequest.isSoldOut());

		return Response.of(productResponse);
	}


	/*
		상품 정보 수정
	*/
	@PatchMapping("/v1/products/{productId}")
	public Response<ProductResponse> editProduct(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable("productId") Long productId,
		@RequestBody ProductRequest productRequest
	){
		ProductResponse productResponse = productService.editProduct(authUser, productId, productRequest);

		return Response.of(productResponse);

	}


}
