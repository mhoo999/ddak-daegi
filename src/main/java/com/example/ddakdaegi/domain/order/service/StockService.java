package com.example.ddakdaegi.domain.order.service;

import com.example.ddakdaegi.domain.order.dto.request.PromotionProductRequest;
import com.example.ddakdaegi.domain.order.dto.response.StockResponse;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.domain.promotion.repository.PromotionProductRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

	private final PromotionProductRepository promotionProductRepository;

	@Transactional
	public StockResponse decreaseStockAndCalculateTotalPrice(List<PromotionProductRequest> promotionProductRequests) {
		List<Long> promotionProductIds = promotionProductRequests.stream()
			.map(PromotionProductRequest::getPromotionProductId)
			.toList();

		Map<Long, Long> promotionProductIdToQuantityMap = promotionProductRequests.stream()
			.collect(
				Collectors.toMap(PromotionProductRequest::getPromotionProductId, PromotionProductRequest::getQuantity));

		List<PromotionProduct> promotionProducts = promotionProductRepository.findAllByIdIn(promotionProductIds);

		long totalPrice = 0L;

		for (PromotionProduct promotionProduct : promotionProducts) {
			Long quantity = promotionProductIdToQuantityMap.get(promotionProduct.getId());
			if (promotionProduct.getStock() < quantity) {
				throw new RuntimeException("재고가 부족합니다.");
			}
			promotionProduct.decreaseStock(quantity);
			log.info("현재 재고 수량: {}", promotionProduct.getStock());
			totalPrice += quantity * promotionProduct.getPrice();
		}

		return StockResponse.of(totalPrice, promotionProductIdToQuantityMap, promotionProducts);
	}

}
