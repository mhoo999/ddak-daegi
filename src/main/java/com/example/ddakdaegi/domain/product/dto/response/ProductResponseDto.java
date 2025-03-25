package com.example.ddakdaegi.domain.product.dto.response;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponseDto {

	private final Long id;

	private final Member member;

	private final String description;

	private final String name;

	private final Image image;

	private final Boolean soldOut;

	private final Long stock;

	private final Long price;


	// Product 클래스를 받는 생성자
	public ProductResponseDto(Product product) {
		this.id = product.getId();
		this.member = product.getMember();
		this.description = product.getDescription();
		this.name = product.getName();
		this.image = product.getImage();
		this.soldOut = product.getSoldOut();
		this.stock = product.getStock();
		this.price = product.getPrice();
	}


	// 페이지네이션 다건조회를 toDto 메서드
	public static ProductResponseDto toDto(Product product) {
		return new ProductResponseDto(
			product.getId(),
			product.getMember(),
			product.getDescription(),
			product.getName(),
			product.getImage(),
			product.getSoldOut(),
			product.getStock(),
			product.getPrice()
		);
	}

}
