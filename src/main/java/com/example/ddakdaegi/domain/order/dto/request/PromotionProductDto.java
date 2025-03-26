package com.example.ddakdaegi.domain.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionProductDto {

	private Long promotionProductId;
	private Long promotionProductPrice;
	private Long quantity;
}
