package com.example.ddakdaegi.domain.order.service;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.domain.order.dto.request.PromotionProductRequest;
import com.example.ddakdaegi.domain.order.dto.response.OrderDetailResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final PromotionProductRepository promotionProductRepository;
	private final OrderRepository orderRepository;
	private final OrderPromotionProductRepository orderPromotionProductRepository;
	private final MemberRepository memberRepository;


	@Transactional
	public OrderResponse createOrder(AuthUser authUser, List<PromotionProductRequest> promotionProductRequests) {
		List<Long> promotionProductIds = promotionProductRequests.stream()
			.map(PromotionProductRequest::getPromotionProductId)
			.collect(Collectors.toList());

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

	@Transactional(readOnly = true)
	public OrderDetailResponse getOrder(Long orderId, AuthUser authUser) {
		Member member = memberRepository.findById(authUser.getId())
			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

		Order getOrder = orderRepository.findByOrderIdAndMemberId(orderId, authUser.getId())
			.orElseThrow(() -> new RuntimeException("주문을 찾을 수 없음"));

		List<OrderPromotionProduct> getOrderPromotionProduct = orderPromotionProductRepository.findByOrderId(orderId);
		return OrderDetailResponse.of(getOrder, member, getOrderPromotionProduct);
	}

	@Transactional(readOnly = true)
	public Page<OrderResponse> getAllOrders(AuthUser authUser, Pageable pageable) {
		return orderRepository.findOrders(authUser.getId(), pageable);
	}

	@Transactional
	public void cancelOrder(Long orderId, AuthUser authUser) {
		Order getOrder = orderRepository.findByOrderIdAndMemberId(orderId, authUser.getId())
			.orElseThrow(() -> new RuntimeException("사용자의 주문을 찾을 수 없음"));

		List<OrderPromotionProduct> getOrderPromotionProduct = orderPromotionProductRepository.findByOrderId(orderId);

		for (OrderPromotionProduct orderPromotionProduct : getOrderPromotionProduct) {
			PromotionProduct promotionProduct = orderPromotionProduct.getPromotionProduct();

			if (promotionProduct.getPromotion().getIsActive()) {
				promotionProduct.revertStock(orderPromotionProduct.getQuantity());
			} else {
				promotionProduct.getProduct().revertStock(orderPromotionProduct.getQuantity());
			}
		}

		getOrder.cancelOrder();
	}
}