package com.example.ddakdaegi.domain.order.entity;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.order.enums.OrderStatus;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
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
}
