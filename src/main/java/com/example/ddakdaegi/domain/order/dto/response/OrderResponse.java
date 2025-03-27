package com.example.ddakdaegi.domain.order.dto.response;

import com.example.ddakdaegi.domain.order.entity.Order;
import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import com.example.ddakdaegi.domain.order.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderResponse {

	private final Long orderId;
	private final List<OrderPromotionProductDto> orderPromotionProductDtos;
	private final Long totalPrice;
	private final OrderStatus status;
	private final LocalDateTime orderCompletionTime;

	public OrderResponse(Long orderId, List<OrderPromotionProductDto> orderPromotionProductDtos, Long totalPrice,
		OrderStatus status, LocalDateTime orderCompletionTime) {
		this.orderId = orderId;
		this.orderPromotionProductDtos = orderPromotionProductDtos;
		this.totalPrice = totalPrice;
		this.status = status;
		this.orderCompletionTime = orderCompletionTime;
	}

	public static OrderResponse of(Order order, List<OrderPromotionProduct> orderPromotionProducts) {
		List<OrderPromotionProductDto> orderPromotionProductDtos =
			orderPromotionProducts.stream().map(OrderPromotionProductDto::of).toList();

		return new OrderResponse(
			order.getId(), orderPromotionProductDtos, order.getTotalPrice(), order.getStatus(), order.getCreatedAt());
	}

	public static OrderResponse ofDto(Order order, List<OrderPromotionProductDto> orderPromotionProductDtos) {
		return new OrderResponse(
			order.getId(), orderPromotionProductDtos, order.getTotalPrice(), order.getStatus(), order.getCreatedAt());
	}
}
