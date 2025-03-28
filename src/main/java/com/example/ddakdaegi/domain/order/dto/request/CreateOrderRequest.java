package com.example.ddakdaegi.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

	@NotNull
	private Long promotionId;

	@NotNull
	@Size(min = 1)
	private List<PromotionProductRequest> promotionProductRequests;

}
