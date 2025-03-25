package com.example.ddakdaegi.domain.promotion.dto.request;

import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePromotionRequestDto {

	private String name;
	private String banner_image;
	private List<PromotionProduct> promotionProducts;
	private LocalDateTime start_date;
	private LocalDateTime end_date;

	public CreatePromotionRequestDto(String name, String banner_image, List<PromotionProduct> promotionProducts,
		LocalDateTime start_date, LocalDateTime end_date) {
		this.name = name;
		this.banner_image = banner_image;
		this.promotionProducts = promotionProducts;
		this.start_date = start_date;
		this.end_date = end_date;
	}
}
