package com.example.ddakdaegi.domain.promotion.service;

import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.request.UpdatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionResponse;
import com.example.ddakdaegi.domain.promotion.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionService {

	private final PromotionRepository promotionRepository;

	public PromotionResponse createPromotion(CreatePromotionRequest dto) {
		return null;
	}

	public PromotionResponse getPromotions() {
		return null;
	}

	public PromotionResponse getPromotionById(Long id) {
		return null;
	}

	public PromotionResponse updatePromotion(Long id, UpdatePromotionRequest request) {
		return null;
	}

	public void deletePromotion(Long id) {
	}
}
