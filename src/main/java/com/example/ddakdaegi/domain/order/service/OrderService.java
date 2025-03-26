package com.example.ddakdaegi.domain.order.service;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.order.dto.request.PromotionProductDto;
import com.example.ddakdaegi.domain.order.dto.response.OrderResponse;
import com.example.ddakdaegi.domain.order.entity.Order;
import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import com.example.ddakdaegi.domain.order.repository.OrderPromotionProductRepository;
import com.example.ddakdaegi.domain.order.repository.OrderRepository;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.domain.promotion.repository.PromotionProductRepository;
import com.example.ddakdaegi.global.common.dto.AuthUser;
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
public class OrderService {

	private final PromotionProductRepository promotionProductRepository;
	private final OrderRepository orderRepository;
	private final OrderPromotionProductRepository orderPromotionProductRepository;


	@Transactional
	public OrderResponse createOrder(AuthUser authUser, List<PromotionProductDto> promotionProductDtos) {
		List<Long> promotionProductIds = promotionProductDtos.stream()
			.map(PromotionProductDto::getPromotionProductId)
			.collect(Collectors.toList());

		Map<Long, Long> promotionProductIdToQuantityMap = promotionProductDtos.stream()
			.collect(Collectors.toMap(PromotionProductDto::getPromotionProductId, PromotionProductDto::getQuantity));

		List<PromotionProduct> promotionProducts = promotionProductRepository.findAllByIdIn(promotionProductIds);

		long totalPrice = 0L;

		for (PromotionProduct promotionProduct : promotionProducts) {
			Long quantity = promotionProductIdToQuantityMap.get(promotionProduct.getId());
			if (promotionProduct.getStock() < quantity) {
				throw new RuntimeException("재고가 부족합니다.");
			}
			promotionProduct.decreaseStock(quantity);
			log.info("현재 재고: {}", promotionProduct.getStock());
			totalPrice += quantity * promotionProduct.getPrice();
		}

		Order newOrder = Order.of(Member.fromAuthUser(authUser), totalPrice);
		orderRepository.save(newOrder);

		List<OrderPromotionProduct> orderPromotionProducts = promotionProducts.stream()
			.map(promotionProduct -> {
				Long quantity = promotionProductIdToQuantityMap.get(promotionProduct.getId());
				return OrderPromotionProduct.of(newOrder, promotionProduct, quantity);
			}).collect(Collectors.toList());

		orderPromotionProductRepository.saveAll(orderPromotionProducts);
		return OrderResponse.of(newOrder, orderPromotionProducts);
	}
}