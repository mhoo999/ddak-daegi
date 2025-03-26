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
	private final List<OrderPromotionProductDto> orderPromotionProducts;
	private final Long totalPrice;
	private final OrderStatus status;
	private final LocalDateTime orderCompletionTime;

	private OrderResponse(Order order, List<OrderPromotionProduct> orderPromotionProducts) {
		this.orderId = order.getId();
		this.orderPromotionProducts = orderPromotionProducts.stream()
			.map(OrderPromotionProductDto::of)
			.toList();
		this.totalPrice = order.getTotalPrice();
		this.status = order.getStatus();
		this.orderCompletionTime = LocalDateTime.now();
	}

	public static OrderResponse of(Order order, List<OrderPromotionProduct> orderPromotionProducts) {
		return new OrderResponse(order, orderPromotionProducts);
	}
}
