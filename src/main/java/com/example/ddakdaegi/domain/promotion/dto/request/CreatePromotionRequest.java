package com.example.ddakdaegi.domain.promotion.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
public class CreatePromotionRequest {

	private String name;
	private String bannerImage;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime startDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime endDate;
	private List<CreatePromotionProductRequest> promotionProducts;

	public CreatePromotionRequest(String name, String bannerImage,
		LocalDateTime startDate, LocalDateTime endDate, List<CreatePromotionProductRequest> promotionProducts) {
		this.name = name;
		this.bannerImage = bannerImage;
		this.startDate = startDate;
		this.endDate = endDate;
		this.promotionProducts = promotionProducts;
	}
}
