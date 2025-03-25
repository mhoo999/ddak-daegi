package com.example.ddakdaegi.domain.promotion.dto.response;

import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PromotionResponse {

	private final Long id;
	private final String name;
//	private final String bannerImage;
	private final LocalDateTime startDate;
	private final LocalDateTime end_date;
	private final boolean isActive;
	private final LocalDateTime created_at;
	private final LocalDateTime updated_at;

	public PromotionResponse(Long id, String name, LocalDateTime startDate,
		LocalDateTime endDate, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
//		this.bannerImage = bannerImage;
		this.startDate = startDate;
		this.end_date = endDate;
		this.isActive = isActive;
		this.created_at = createdAt;
		this.updated_at = updatedAt;
	}

	public static PromotionResponse from(Promotion promotion) {
		return new PromotionResponse(
			promotion.getId(),
			promotion.getName(),
//			promotion.getBanner() != null ? promotion.getBanner().getUrl() : null,
			promotion.getStartTime(),
			promotion.getEndTime(),
			promotion.getIsActive(),
			promotion.getCreatedAt(),
			promotion.getUpdatedAt()
		);
	}
}
