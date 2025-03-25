package com.example.ddakdaegi.domain.promotion.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePromotionRequest {

	private String name;
	private String banner_image;
	private LocalDateTime start_date;
	private LocalDateTime end_date;

	public UpdatePromotionRequest(String name, String banner_image, LocalDateTime start_date,
		LocalDateTime end_date) {
		this.name = name;
		this.banner_image = banner_image;
		this.start_date = start_date;
		this.end_date = end_date;
	}

}
