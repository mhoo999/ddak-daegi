package com.example.ddakdaegi.domain.promotion.dto.request;

import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromotionProductRequest {

	private Long productId;
	private Long stock;
	private Long purchaseLimit;
	private DiscountPolicy discountPolicy;
	private Long discountValue;

}
