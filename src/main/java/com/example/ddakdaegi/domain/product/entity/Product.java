package com.example.ddakdaegi.domain.product.entity;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "product")
@Getter
@Setter
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

	/*
		soft delete 를 위한 변수
		Product 어트리뷰트를 삭제하는 순간 날짜 시간이 대입을 하고
		데이터가 DB에 논리적으로 존재하지 않는다고 취급
	*/
	@Column
	@ColumnDefault("null") // 기본값 null -> 데이터가 DB에 논리적으로 존재
	private LocalDateTime deletedAt;

	// 생성자
	public Product(
		Member member,
		String description,
		String name,
		Image image,
		Long stock,
		Long price
	) {
		this.member = member;
		this.description = description;
		this.name = name;
		this.image = image;
		this.stock = stock;
		this.price = price;

		this.soldOut = false; // 기본 값
		//this.deletedAt = null;
	}

	public void revertStock(Long quantity) {
		this.stock += quantity;
	}
}
