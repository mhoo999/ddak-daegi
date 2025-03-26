package com.example.ddakdaegi.domain.order.dto.response;

import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import lombok.Getter;

@Getter
public class OrderPromotionProductDto {

	private final Long orderPromotionProductId;
	private final PromotionProduct promotionProduct;
	private final Long quantity;

	private OrderPromotionProductDto(OrderPromotionProduct orderPromotionProduct) {
		this.orderPromotionProductId = orderPromotionProduct.getId();
		this.promotionProduct = orderPromotionProduct.getPromotionProduct();
		this.quantity = orderPromotionProduct.getQuantity();
	}

	public static OrderPromotionProductDto of(OrderPromotionProduct orderPromotionProduct) {
		return new OrderPromotionProductDto(orderPromotionProduct);
	}
}
