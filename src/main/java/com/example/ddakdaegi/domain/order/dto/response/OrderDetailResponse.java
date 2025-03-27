package com.example.ddakdaegi.domain.order.dto.response;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.order.entity.Order;
import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import com.example.ddakdaegi.domain.order.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderDetailResponse {

	private final Long orderId;
	private final String email;
	private final String address;
	private final List<OrderPromotionProductDto> orderPromotionProducts;
	private final Long totalPrice;
	private final OrderStatus status;
	private final LocalDateTime orderCompletionTime;

	private OrderDetailResponse(Order order, Member member, List<OrderPromotionProduct> orderPromotionProducts) {
		this.orderId = order.getId();
		this.email = member.getEmail();
		this.address = member.getAddress();
		this.orderPromotionProducts = orderPromotionProducts.stream()
			.map(OrderPromotionProductDto::of)
			.toList();
		this.totalPrice = order.getTotalPrice();
		this.status = order.getStatus();
		this.orderCompletionTime = order.getCreatedAt();
	}

	public static OrderDetailResponse of(Order order, Member member,
		List<OrderPromotionProduct> orderPromotionProducts) {
		return new OrderDetailResponse(order, member, orderPromotionProducts);
	}

}
