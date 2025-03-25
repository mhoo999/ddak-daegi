package com.example.ddakdaegi.domain.promotion.controller;

import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionRequestDto;
import com.example.ddakdaegi.domain.promotion.dto.request.UpdatePromotionRequestDto;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionResponseDto;
import com.example.ddakdaegi.domain.promotion.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ResponseEntity<PromotionResponseDto> createPromotion(@RequestBody CreatePromotionRequestDto request) {
		return ResponseEntity.ok(promotionService.create(request));
	}

	@GetMapping("/v1/promotions")
	public ResponseEntity<PromotionResponseDto> getPromotions() {
		return ResponseEntity.ok(promotionService.getAll());
	}

	@GetMapping("/v1/promotions/{id}")
	public ResponseEntity<PromotionResponseDto> getPromotionById(@PathVariable Long id) {
		return ResponseEntity.ok(promotionService.get(id));
	}

	@PatchMapping("/v1/promotions/{id}")
	public ResponseEntity<PromotionResponseDto> updatePromotion(@PathVariable Long id, @RequestBody
	UpdatePromotionRequestDto request) {
		return ResponseEntity.ok(promotionService.update(id, request));
	}

	@DeleteMapping("/v1/promotions/{id}")
	public void deletePromotion(@PathVariable Long id) {
		promotionService.delete(id);
	}
}
