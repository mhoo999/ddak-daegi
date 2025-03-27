package com.example.ddakdaegi.domain.product.dto.request;


import com.example.ddakdaegi.domain.image.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ProductRequest {

	private final String description;

	private final String name;

	private final Long image;

	private final Long stock;

	private final Long price;

	// 생성자 나중에 쓸지 혹시몰라서 주석처리함 -> @AllArgsConstructor 으로 대체
	/*
	public ProductRequestDto(
		String description,
		String name,
		Image image,
		Long stock,
		Long price
	) {
		this.description = description;
		this.name = name;
		this.image = image;
		this.stock = stock;
		this.price = price;
	}*/
}
