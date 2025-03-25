package com.example.ddakdaegi.domain.promotion.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class PromotionResponse {

	private final Long id;
	private final String name;
	private final String banner_image;
	private final LocalDateTime start_date;
	private final LocalDateTime end_date;
	private final boolean isActive;
	private final LocalDateTime created_at;
	private final LocalDateTime updated_at;

	public PromotionResponse(Long id, String name, String banner_image, LocalDateTime start_date,
		LocalDateTime end_date, boolean isActive, LocalDateTime created_at, LocalDateTime updated_at) {
		this.id = id;
		this.name = name;
		this.banner_image = banner_image;
		this.start_date = start_date;
		this.end_date = end_date;
		this.isActive = isActive;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}
}
