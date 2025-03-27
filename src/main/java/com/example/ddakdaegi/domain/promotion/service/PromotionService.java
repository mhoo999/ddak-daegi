package com.example.ddakdaegi.domain.promotion.service;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.*;

import com.example.ddakdaegi.domain.image.entity.Image;
import com.example.ddakdaegi.domain.product.entity.Product;
import com.example.ddakdaegi.domain.product.repository.ProductRepository;
import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionProductRequest;
import com.example.ddakdaegi.domain.promotion.dto.request.CreatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.request.UpdatePromotionRequest;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionResponse;
import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.domain.promotion.enums.DiscountPolicy;
import com.example.ddakdaegi.domain.promotion.repository.PromotionProductRepository;
import com.example.ddakdaegi.domain.promotion.repository.PromotionRepository;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.scheduler.PromotionSchedulerService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PromotionService {

	private final PromotionRepository promotionRepository;
	private final ProductRepository productRepository;
	private final PromotionProductRepository promotionProductRepository;
	private final PromotionSchedulerService schedulerService;

	@Transactional
	public PromotionResponse createPromotion(AuthUser authUser, CreatePromotionRequest request) {
		if (request.getStartDate().isAfter(request.getEndDate())) {
			throw new BaseException(INVALID_DATE_RANGE);
		}

		Image newBanner = null;

		Promotion promotion = new Promotion(
			request.getName(),
			newBanner,
			request.getStartDate(),
			request.getEndDate(),
			false
		);

		promotionRepository.save(promotion);

		for (CreatePromotionProductRequest ppRequest : request.getPromotionProducts()) {
			Product product = productRepository.findById(ppRequest.getProductId())
				.orElseThrow(() -> new BaseException(NOT_FOUND_PRODUCT));

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
				promotion,
				product,
				ppRequest.getStock(),
				ppRequest.getPurchaseLimit(),
				ppRequest.getDiscountPolicy(),
				ppRequest.getDiscountValue(),
				discountedPrice
			);

			promotionProductRepository.save(promotionProduct);
		}

		schedulerService.schedulePromotion(promotion.getId(), promotion.getStartTime(), true);   // 활성화 예약
		schedulerService.schedulePromotion(promotion.getId(), promotion.getEndTime(), false);    // 비활성화 예약

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

	@Transactional(readOnly = true)
	public List<PromotionResponse> getPromotions() {
		List<Promotion> promotions = promotionRepository.findAll();

		return promotions.stream()
			.map(PromotionResponse::from)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
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

	@Transactional
	public PromotionResponse updatePromotion(Long promotionId, UpdatePromotionRequest request) {
		if (request.getStartDate().isAfter(request.getEndDate())) {
			throw new BaseException(INVALID_DATE_RANGE);
		}

		Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new BaseException(NOT_FOUND_PROMOTION));

		Image newBanner = null;
		promotion.update(
			request.getName(),
			newBanner,
			request.getStartDate(),
			request.getEndDate()
		);

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
		BigDecimal original = BigDecimal.valueOf(originalPrice);
		BigDecimal result;

		if (policy == DiscountPolicy.RATE) {
			BigDecimal rate = BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
			result = original.multiply(BigDecimal.ONE.subtract(rate));
		} else if (policy == DiscountPolicy.FIXED) {
			result = original.subtract(BigDecimal.valueOf(value));
			if (result.compareTo(BigDecimal.ZERO) < 0) {
				result = BigDecimal.ZERO;
			}
		} else {
			throw new BaseException(WRONG_POLICY);
		}

		return result.setScale(0, RoundingMode.HALF_UP).longValue();
	}

	@Transactional
	public void terminatePromotion(Long promotionId) {
		Promotion promotion = promotionRepository.findById(promotionId).orElseThrow(() -> new BaseException(NOT_FOUND_PROMOTION));
		promotion.terminate();
	}
}
