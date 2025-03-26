package com.example.ddakdaegi.domain.promotion.dto.request;

import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePromotionProductRequest {

	private Long productId;
	private Long stock;
	private Long purchaseLimit;
	private DiscountPolicy discountPolicy;
	private Long discountValue;

	public CreatePromotionProductRequest(Long productId, Long stock, Long purchaseLimit, DiscountPolicy discountPolicy,
		Long discountValue) {
		this.productId = productId;
		this.stock = stock;
		this.purchaseLimit = purchaseLimit;
		this.discountPolicy = discountPolicy;
		this.discountValue = discountValue;
	}
}
