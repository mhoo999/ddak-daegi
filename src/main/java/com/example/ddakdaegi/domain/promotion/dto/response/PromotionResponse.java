package com.example.ddakdaegi.domain.promotion.dto.response;

import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PromotionResponse {

	private final Long id;
	private final String name;
//	private final String bannerImage;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final boolean isActive;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public static PromotionResponse toDto(Promotion promotion) {
		return new PromotionResponse(
			promotion.getId(),
			promotion.getName(),
//			promotion.getBanner() != null ? promotion.getBanner().getUrl() : null,
			promotion.getStartDate(),
			promotion.getEndDate(),
			promotion.getIsActive(),
			promotion.getCreatedAt(),
			promotion.getUpdatedAt()
		);
	}
}
