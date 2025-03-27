package com.example.ddakdaegi.domain.order.dto.response;

import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionProductDto;
import lombok.Getter;

@Getter
public class OrderPromotionProductDto {

	private final Long orderPromotionProductId;
	private final PromotionProductDto promotionProduct;
	private final Long quantity;

	private OrderPromotionProductDto(
		Long orderPromotionProductId,
		OrderPromotionProduct orderPromotionProduct,
		Long quantity
	) {
		this.orderPromotionProductId = orderPromotionProductId;
		this.promotionProduct = PromotionProductDto.of(orderPromotionProduct);
		this.quantity = quantity;
	}

	// Querydsl 사용을 위한 생성자
	public OrderPromotionProductDto(
		Long orderPromotionProductId,
		PromotionProductDto promotionProductDto,
		Long quantity
	) {
		this.orderPromotionProductId = orderPromotionProductId;
		this.promotionProduct = promotionProductDto;
		this.quantity = quantity;
	}

	public static OrderPromotionProductDto of(OrderPromotionProduct orderPromotionProduct) {
		return new OrderPromotionProductDto(
			orderPromotionProduct.getId(),
			orderPromotionProduct,
			orderPromotionProduct.getQuantity()
		);
	}
}
