package com.example.ddakdaegi.domain.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionProductRequest {

	private Long promotionProductId;
	private Long quantity;
}
