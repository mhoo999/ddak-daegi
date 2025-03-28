package com.example.ddakdaegi.domain.order.controller;

import com.example.ddakdaegi.domain.order.dto.request.CreateOrderRequest;
import com.example.ddakdaegi.domain.order.dto.response.OrderDetailResponse;
import com.example.ddakdaegi.domain.order.dto.response.OrderResponse;
import com.example.ddakdaegi.domain.order.dto.response.StockResponse;
import com.example.ddakdaegi.domain.order.service.OrderService;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.response.Response;
import com.example.ddakdaegi.global.util.lock.RedissonLockStockFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final RedissonLockStockFacade stockFacade;

	@PostMapping("/v1/orders")
	public Response<OrderResponse> createOrder(
		@AuthenticationPrincipal AuthUser authUser,
		@Valid @RequestBody CreateOrderRequest request
	) {
		StockResponse stockResponse =
			stockFacade.lockAndDecreaseStock(request.getPromotionId(), request.getPromotionProductRequests());

		OrderResponse orderResponse =
			orderService.createOrder(authUser, stockResponse);
		return Response.of(orderResponse);
	}

	@GetMapping("/v1/orders/{orderId}")
	public Response<OrderDetailResponse> getOrder(
		@PathVariable Long orderId,
		@AuthenticationPrincipal AuthUser authUser
	) {
		return Response.of(orderService.getOrder(orderId, authUser));
	}

	@GetMapping("/v1/orders")
	public Response<Page<OrderResponse>> getAllOrders(
		@AuthenticationPrincipal AuthUser authUser,
		@PageableDefault Pageable pageable
	) {
		return Response.of(orderService.getAllOrders(authUser, pageable));
	}

	@PatchMapping("/v1/orders/{orderId}")
	public void cancelOrder(@PathVariable Long orderId, @AuthenticationPrincipal AuthUser authUser) {
		orderService.cancelOrder(orderId, authUser);
	}
}
