package com.example.ddakdaegi.domain.order.controller;

import com.example.ddakdaegi.domain.order.dto.request.CreateOrderRequest;
import com.example.ddakdaegi.domain.order.dto.response.OrderResponse;
import com.example.ddakdaegi.domain.order.service.OrderService;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/v1/orders")
	public Response<OrderResponse> createOrder(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody CreateOrderRequest request
	) {

		OrderResponse orderResponse =
			orderService.createOrder(authUser, request.getPromotionProductDtos());
		return Response.of(orderResponse);
	}

}
