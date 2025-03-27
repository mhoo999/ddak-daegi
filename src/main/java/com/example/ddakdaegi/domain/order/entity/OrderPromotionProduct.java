package com.example.ddakdaegi.domain.order.entity;

import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderPromotionProduct extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "orders_id")
	private Order order;

	@ManyToOne
	@JoinColumn(name = "promotion_product_id")
	private PromotionProduct promotionProduct;

	@Column(nullable = false)
	private Long quantity;

	private OrderPromotionProduct(Order order, PromotionProduct promotionProduct, Long quantity) {
		this.order = order;
		this.promotionProduct = promotionProduct;
		this.quantity = quantity;
	}

	public static OrderPromotionProduct of(Order order, PromotionProduct promotionProduct, Long quantity) {
		return new OrderPromotionProduct(order, promotionProduct, quantity);
	}
}
