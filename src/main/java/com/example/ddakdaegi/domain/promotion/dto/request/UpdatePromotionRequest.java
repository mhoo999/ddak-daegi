package com.example.ddakdaegi.domain.promotion.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePromotionRequest {

	private String name;
	private String bannerImage;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	public UpdatePromotionRequest(String name, String bannerImage, LocalDateTime startDate,
		LocalDateTime endDate) {
		this.name = name;
		this.bannerImage = bannerImage;
		this.startDate = startDate;
		this.endDate = endDate;
	}

}
