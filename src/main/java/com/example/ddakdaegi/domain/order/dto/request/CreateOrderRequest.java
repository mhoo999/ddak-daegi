package com.example.ddakdaegi.domain.order.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

	private List<PromotionProductRequest> promotionProductRequests;

}
