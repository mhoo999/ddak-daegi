package com.example.ddakdaegi.domain.order.dto.response;

import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class StockResponse {

	private final Long totalPrice;
	private final Map<Long, Long> promotionProductIdToQuantityMap;
	private final List<PromotionProduct> promotionProducts;

	private StockResponse(Long totalPrice, Map<Long, Long> promotionProductIdToQuantityMap,
		List<PromotionProduct> promotionProducts) {
		this.totalPrice = totalPrice;
		this.promotionProductIdToQuantityMap = promotionProductIdToQuantityMap;
		this.promotionProducts = promotionProducts;
	}

	public static StockResponse of(Long totalPrice, Map<Long, Long> promotionProductIdToQuantityMap,
		List<PromotionProduct> promotionProducts) {
		return new StockResponse(totalPrice, promotionProductIdToQuantityMap, promotionProducts);
	}
}
