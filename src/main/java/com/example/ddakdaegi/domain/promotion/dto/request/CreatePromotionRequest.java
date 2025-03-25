package com.example.ddakdaegi.domain.promotion.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePromotionRequest {

	private String name;
	private String bannerImage;
	private List<CreatePromotionProductRequest> promotionProducts;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	public CreatePromotionRequest(String name, String banner_image, List<CreatePromotionProductRequest> promotionProducts,
		LocalDateTime startDate, LocalDateTime endDate) {
		this.name = name;
		this.bannerImage = banner_image;
		this.promotionProducts = promotionProducts;
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
