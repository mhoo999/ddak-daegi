package com.example.ddakdaegi.domain.product.controller;

import com.example.ddakdaegi.domain.product.dto.request.ProductRequest;
import com.example.ddakdaegi.domain.product.dto.response.ProductResponse;
import com.example.ddakdaegi.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
	public ResponseEntity<ProductResponse> saveProduct(
		//@PathVariable Long memberId,
		@RequestBody ProductRequest productRequest
	) {

		ProductResponse productResponse = productService.saveProduct(/*memberId,*/ productRequest);
		return new ResponseEntity<>(productResponse, HttpStatus.CREATED);

	}


	/*
		Product(상품) 다건 조회 메서드
	*/
	@GetMapping("/v1/products")
	public ResponseEntity<Page<ProductResponse>> findAllProduct(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "5") int size
	) {

		Page<ProductResponse> productResponseDtoPage = productService.findAllProduct(page, size);

		return new ResponseEntity<>(productResponseDtoPage, HttpStatus.OK);

	}


}
