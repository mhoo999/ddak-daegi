package com.example.ddakdaegi.domain.promotion.entity;

import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class PromotionProduct extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "promotion_id")
	private Promotion promotion;

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

	public PromotionProduct(Promotion promotion, Product product, Long stock, Long purchaseLimit,
		DiscountPolicy discountPolicy, Long discountValue, Long price) {
		this.promotion = promotion;
		this.product = product;
		this.stock = stock;
		this.purchaseLimit = purchaseLimit;
		this.discountPolicy = discountPolicy;
		this.discountValue = discountValue;
		this.price = price;
	}

	public void decreaseStock(Long quantity) {
		this.stock -= quantity;
	}

	public void revertStock(Long quantity) {
		this.stock += quantity;
	}
}
