package com.example.ddakdaegi.domain.promotion.controller;

import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.request.UpdatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionResponse;
import com.example.ddakdaegi.domain.promotion.service.PromotionService;
import com.example.ddakdaegi.global.common.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PromotionController {

	private final PromotionService promotionService;

	@PostMapping("/v1/promotions")
	public Response<PromotionResponse> createPromotion(
		@Valid @RequestBody CreatePromotionRequest request
	) {
		return Response.of(promotionService.createPromotion(request));
	}

	@GetMapping("/v1/promotions")
	public Response<Page<PromotionResponse>> findAllPromotion(
		@PageableDefault(page = 1, size = 5) Pageable pageable
	) {
		Page<PromotionResponse> promotionResponseDtoPage = promotionService.findAllPromotion(pageable);
		return Response.of(promotionResponseDtoPage);
	}

	@GetMapping("/v1/promotions/{id}")
	public Response<PromotionResponse> getPromotionById(@PathVariable Long id) {
		return Response.of(promotionService.getPromotionById(id));
	}

	@PatchMapping("/v1/promotions/{id}")
	public Response<PromotionResponse> updatePromotion(
		@PathVariable Long id,
		@RequestBody UpdatePromotionRequest request) {
		return Response.of(promotionService.updatePromotion(id, request));
	}

	@DeleteMapping("/v1/promotions/{id}")
	public void deletePromotion(@PathVariable Long id) {
		promotionService.deletePromotion(id);
	}
}
