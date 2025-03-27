package com.example.ddakdaegi.domain.product.entity;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.global.common.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

	@Column
	@ColumnDefault("null")
	private LocalDateTime deletedAt; // soft delete 를 위한 변수

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
	}

	public void revertStock(Long quantity) {
		this.stock += quantity;
	}
}
