package com.example.ddakdaegi.domain.promotion.controller;

import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.request.UpdatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionResponse;
import com.example.ddakdaegi.domain.promotion.service.PromotionService;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.response.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class PromotionController {

	private final PromotionService promotionService;

	@PostMapping("/v1/promotions")
	public Response<PromotionResponse> createPromotion(
		@AuthenticationPrincipal AuthUser user,
		@RequestBody CreatePromotionRequest request
	) {
		return Response.of(promotionService.createPromotion(user, request));
	}

	@GetMapping("/v1/promotions")
	public Response<List<PromotionResponse>> getPromotions() {
		return Response.of(promotionService.getPromotions());
	}

	@GetMapping("/v1/promotions/{id}")
	public Response<PromotionResponse> getPromotionById(@PathVariable Long id) {
		return Response.of(promotionService.getPromotionById(id));
	}

	@PatchMapping("/v1/promotions/{id}")
	public Response<PromotionResponse> updatePromotion(@PathVariable Long id, @RequestBody
	UpdatePromotionRequest request) {
		return Response.of(promotionService.updatePromotion(id, request));
	}

	@DeleteMapping("/v1/promotions/{id}")
	public void deletePromotion(@PathVariable Long id) {
		promotionService.deletePromotion(id);
	}

	@PatchMapping("/v1/promotions/{id}/terminate")
	public void terminatePromotion(@PathVariable Long id) {
		promotionService.terminatePromotion(id);
	}
}
