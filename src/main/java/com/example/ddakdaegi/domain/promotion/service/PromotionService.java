package com.example.ddakdaegi.domain.promotion.service;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.*;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionProductRequest;
import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.request.UpdatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionResponse;
import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import com.example.ddakdaegi.domain.promotion.repository.PromotionProductRepository;
import com.example.ddakdaegi.domain.promotion.repository.PromotionRepository;
import com.example.ddakdaegi.global.common.exception.BaseException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionService {

	private final PromotionRepository promotionRepository;
//	private final ProductRepository productRepository;
	private final PromotionProductRepository promotionProductRepository;

	@Transactional
	public PromotionResponse createPromotion(CreatePromotionRequest request) {
		if (request.getStart_date().isAfter(request.getEnd_date())) {
			throw new BaseException(INVALID_DATE_RANGE);
		}

		Image newBanner = null;

		Promotion promotion = Promotion.create(request.getName(), newBanner, request.getStart_date(), request.getEnd_date());
		promotionRepository.save(promotion);

		for (CreatePromotionProductRequest ppRequest : request.getPromotionProducts()) {
//			Product product = productRepository.findById(ppRequest.getProductId()).orElseThrow(() -> new BaseException(NOT_FOUND_PRODUCT));
			Product product = null;

			if (product.getSoldOut()) {
				throw new BaseException(SOLD_OUT);
			}

			if (ppRequest.getStock() > product.getStock()) {
				throw new BaseException(OVER_STOCK);
			}

			Long discountedPrice = calculateDiscountedPrice(
				product.getPrice(),
				ppRequest.getDiscountPolicy(),
				ppRequest.getDiscountValue()
			);

			PromotionProduct promotionProduct = new PromotionProduct(
				product,
				ppRequest.getStock(),
				ppRequest.getPurchaseLimit(),
				ppRequest.getDiscountPolicy(),
				ppRequest.getDiscountValue(),
				discountedPrice
			);

			promotion.addPromotionProduct(promotionProduct);
		}

		promotionRepository.save(promotion);

		return new PromotionResponse(
			promotion.getId(),
			promotion.getName(),
			promotion.getStartTime(),
			promotion.getEndTime(),
			promotion.getIsActive(),
			promotion.getCreatedAt(),
			promotion.getUpdatedAt()
		);
	}

	public List<PromotionResponse> getPromotions() {
		List<Promotion> promotions = promotionRepository.findAll();

		return promotions.stream()
			.map(PromotionResponse::from)
			.collect(Collectors.toList());
	}

	public PromotionResponse getPromotionById(Long promotionId) {
		Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new BaseException(NOT_FOUND_PROMOTION));

		return new PromotionResponse(
			promotion.getId(),
			promotion.getName(),
			promotion.getStartTime(),
			promotion.getEndTime(),
			promotion.getIsActive(),
			promotion.getCreatedAt(),
			promotion.getUpdatedAt()
		);
	}

	public PromotionResponse updatePromotion(Long promotionId, UpdatePromotionRequest request) {
		if (request.getStart_date().isAfter(request.getEnd_date())) {
			throw new BaseException(INVALID_DATE_RANGE);
		}

		Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new BaseException(NOT_FOUND_PROMOTION));

		Image newBanner = null;
		promotion.update(request, newBanner);

		return new PromotionResponse(
			promotion.getId(),
			promotion.getName(),
			promotion.getStartTime(),
			promotion.getEndTime(),
			promotion.getIsActive(),
			promotion.getCreatedAt(),
			promotion.getUpdatedAt()
		);
	}

	@Transactional
	public void deletePromotion(Long promotionId) {
		Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new BaseException(NOT_FOUND_PROMOTION));
		promotionRepository.delete(promotion);
	}

	private Long calculateDiscountedPrice(Long originalPrice, DiscountPolicy policy, Long value) {
		if (policy == DiscountPolicy.RATE) {
			double rate = value / 100.0;
			return Math.round(originalPrice * (1 - rate));
		} else if (policy == DiscountPolicy.FIXED) {
			return Math.max(0, originalPrice - value);
		} else {
			throw new BaseException(WRONG_POLICY);
		}
	}
}
