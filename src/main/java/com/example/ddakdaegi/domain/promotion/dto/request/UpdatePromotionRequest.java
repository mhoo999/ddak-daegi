package com.example.ddakdaegi.domain.promotion.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePromotionRequest {

	private final String name;
	private final Long image;
	private final LocalDateTime startDate;
	private final LocalDateTime endDate;
	private final Boolean isTerminate;

}
