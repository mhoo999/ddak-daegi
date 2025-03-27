package com.example.ddakdaegi.domain.order.dto.response;

import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionProductDto;
import lombok.Getter;

@Getter
public class OrderPromotionProductDto {

	private final Long orderPromotionProductId;
	private final PromotionProductDto promotionProduct;
	private final Long quantity;

	public OrderPromotionProductDto(OrderPromotionProduct orderPromotionProduct) {
		this.orderPromotionProductId = orderPromotionProduct.getId();
		this.promotionProduct = PromotionProductDto.of(orderPromotionProduct);
		this.quantity = orderPromotionProduct.getQuantity();
	}

	public static OrderPromotionProductDto of(OrderPromotionProduct orderPromotionProduct) {
		return new OrderPromotionProductDto(orderPromotionProduct);
	}
}
