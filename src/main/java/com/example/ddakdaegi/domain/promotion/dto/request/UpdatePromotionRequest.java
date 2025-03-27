package com.example.ddakdaegi.domain.promotion.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePromotionRequest {

	private String name;
	private String bannerImage;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private Boolean isTerminate;

}
