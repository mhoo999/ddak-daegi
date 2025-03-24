package com.example.ddakdaegi.domain.product.entity;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private String description;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;

    @Column(nullable = false)
    private Boolean soldOut;

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private Long price;

    @Column
    private LocalDateTime deletedAt;

}
