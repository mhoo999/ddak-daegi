package com.example.ddakdaegi.domain.product.dto.response;

import com.example.ddakdaegi.domain.image.dto.response.ImageResponse;
import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.member.dto.response.MemberResponse;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {

	private final Long id;

	private final MemberResponse member;

	private final String description;

	private final String name;

	private final ImageResponse image;

	private final Boolean soldOut;

	private final Long stock;

	private final Long price;


	// Product 클래스를 받는 생성자
	public ProductResponse(Product product) {
		this.id = product.getId();
		this.member = MemberResponse.from(product.getMember());
		this.description = product.getDescription();
		this.name = product.getName();
		this.image = ImageResponse.from(product.getImage());
		this.soldOut = product.getSoldOut();
		this.stock = product.getStock();
		this.price = product.getPrice();
	}


	// 페이지네이션 다건조회를 toDto 메서드
	public static ProductResponse toDto(Product product) {
		return new ProductResponse(
			product.getId(),
			MemberResponse.from(product.getMember()),
			product.getDescription(),
			product.getName(),
			ImageResponse.from(product.getImage()),
			product.getSoldOut(),
			product.getStock(),
			product.getPrice()
		);
	}

}
