package com.example.ddakdaegi.domain.promotion.entity;

import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionProduct extends Timestamped {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(nullable = false)
	private Long stock;

	@Column(nullable = false)
	private Long purchaseLimit;

	@Enumerated(EnumType.STRING)
	private DiscountPolicy discountPolicy;

	@Column(nullable = false)
	private Long discountValue;

	@Column(nullable = false)
	private Long price;

	public PromotionProduct(Product product, Long stock, Long purchaseLimit,
		DiscountPolicy discountPolicy, Long discountValue, Long price) {
		this.product = product;
		this.stock = stock;
		this.purchaseLimit = purchaseLimit;
		this.discountPolicy = discountPolicy;
		this.discountValue = discountValue;
		this.price = price;
	}
}
