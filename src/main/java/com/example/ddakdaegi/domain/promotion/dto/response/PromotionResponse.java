package com.example.ddakdaegi.domain.promotion.dto.response;

import com.example.ddakdaegi.domain.image.dto.response.ImageResponse;
import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PromotionResponse {

	private final Long id;
	private final String name;
	private final ImageResponse bannerImage;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final boolean isActive;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public PromotionResponse(Promotion promotion) {
		this.id = promotion.getId();
		this.name = promotion.getName();
		this.bannerImage = new ImageResponse(promotion.getBanner().getId(), promotion.getBanner().getImageUrl(), promotion.getBanner().getFileName());
		this.startDate = promotion.getStartDate();
		this.endDate = promotion.getEndDate();
		this.isActive = promotion.getIsActive();
		this.createdAt = promotion.getCreatedAt();
		this.updatedAt = promotion.getUpdatedAt();
	}

	public static PromotionResponse toDto(Promotion promotion) {
		return new PromotionResponse(
			promotion.getId(),
			promotion.getName(),
			new ImageResponse(promotion.getBanner().getId(), promotion.getBanner().getImageUrl(), promotion.getBanner().getFileName()),
			promotion.getStartDate(),
			promotion.getEndDate(),
			promotion.getIsActive(),
			promotion.getCreatedAt(),
			promotion.getUpdatedAt()
		);
	}
}
