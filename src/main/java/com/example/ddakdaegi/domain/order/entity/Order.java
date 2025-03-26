package com.example.ddakdaegi.domain.order.entity;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.order.enums.OrderStatus;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@Column(nullable = false)
	private Long totalPrice;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	private Order(Member member, Long totalPrice) {
		this.member = member;
		this.totalPrice = totalPrice;
		this.status = OrderStatus.COMPLETED;
	}

	public static Order of(Member member, Long totalPrice) {
		return new Order(member, totalPrice);
	}
}
