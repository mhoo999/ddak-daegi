package com.example.ddakdaegi.domain.promotion.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePromotionRequest {

	@NotBlank(message = "프로모션 이름은 필수 입력 사항입니다.")
	private String name;

	private String bannerImage;

	@NotNull(message = "시작 날짜는 필수 입력 항목입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime startDate;

	@NotNull(message="종료 날짜는 필수 입력 항목입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime endDate;

	@NotNull(message="프로모션 상품 리스트는 필수 입력 항목입니다.")
	@Size(min = 1, message ="최소 하나의 상품이 포함되어야 합니다.")
	private List<CreatePromotionProductRequest> promotionProducts;

}
