package com.example.ddakdaegi.domain.promotion.service;

import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionRequestDto;
import com.example.ddakdaegi.domain.promotion.dto.request.UpdatePromotionRequestDto;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionResponseDto;
import com.example.ddakdaegi.domain.promotion.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionService {

	private final PromotionRepository promotionRepository;

	public PromotionResponseDto create(CreatePromotionRequestDto dto) {
		return null;
	}

	public PromotionResponseDto getAll() {
		return null;
	}

	public PromotionResponseDto get(Long id) {
		return null;
	}

	public PromotionResponseDto update(Long id, UpdatePromotionRequestDto request) {
		return null;
	}

	public void delete(Long id) {
	}
}
