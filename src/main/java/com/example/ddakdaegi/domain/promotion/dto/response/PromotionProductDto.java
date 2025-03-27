package com.example.ddakdaegi.domain.promotion.dto.response;

import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import lombok.Getter;

@Getter
public class PromotionProductDto {

	private final Long promotionProductId;
	private final String productName;
	private final String productImage;
	private final Long price;

	public PromotionProductDto(Long promotionProductId, String productName, String productImage, Long price) {
		this.promotionProductId = promotionProductId;
		this.productName = productName;
		this.productImage = productImage;
		this.price = price;
	}

	public static PromotionProductDto of(OrderPromotionProduct orderPromotionProduct) {
		PromotionProduct promotionProduct = orderPromotionProduct.getPromotionProduct();
		return new PromotionProductDto(
			promotionProduct.getId(),
			promotionProduct.getProduct().getName(),
			promotionProduct.getProduct().getImage().getImageUrl(),
			promotionProduct.getPrice()
		);
	}
}
