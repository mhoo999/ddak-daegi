package com.example.ddakdaegi.domain.order.service;

import com.example.ddakdaegi.domain.order.dto.request.PromotionProductRequest;
import com.example.ddakdaegi.domain.order.dto.response.StockResponse;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.domain.promotion.repository.PromotionProductRepository;
import com.example.ddakdaegi.domain.promotion.repository.PromotionRepository;
import com.example.ddakdaegi.global.common.exception.BaseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.INSUFFICIENT_PROMOTION_PRODUCT_STOCK;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.NOT_FOUND_PROMOTION_PRODUCT;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.PROMOTION_NOT_STARTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

	private final PromotionProductRepository promotionProductRepository;
	private final PromotionRepository promotionRepository;

	@Transactional
	public StockResponse decreaseStockAndCalculateTotalPrice(
		Long promotionId,
		List<PromotionProductRequest> promotionProductRequests
	) {
		if (!promotionRepository.promotionIsActive(promotionId)) {
			throw new BaseException(PROMOTION_NOT_STARTED);
		}

		List<Long> promotionProductIds = promotionProductRequests.stream()
			.map(PromotionProductRequest::getPromotionProductId)
			.toList();

		Map<Long, Long> promotionProductIdToQuantityMap = promotionProductRequests.stream()
			.collect(
				Collectors.toMap(PromotionProductRequest::getPromotionProductId, PromotionProductRequest::getQuantity));

		List<PromotionProduct> promotionProducts = promotionProductRepository.findAllByIdIn(promotionProductIds);

		if (promotionProducts.isEmpty()) {
			throw new BaseException(NOT_FOUND_PROMOTION_PRODUCT);
		}

		long totalPrice = promotionProducts.stream()
			.mapToLong(promotionProduct -> {
				Long quantity = promotionProductIdToQuantityMap.get(promotionProduct.getId());
				if (promotionProduct.getStock() < quantity) {
					throw new BaseException(INSUFFICIENT_PROMOTION_PRODUCT_STOCK);
				}
				promotionProduct.decreaseStock(quantity);
				log.info("재고 ID: {}, STOCK: {}, THREAD NAME: {}", promotionProduct.getId(),
					promotionProduct.getStock(), Thread.currentThread().getName());
				return quantity * promotionProduct.getPrice();
			}).sum();

		return StockResponse.of(totalPrice, promotionProductIdToQuantityMap, promotionProducts);
	}

}
