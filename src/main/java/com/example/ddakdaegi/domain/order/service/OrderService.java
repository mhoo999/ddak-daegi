package com.example.ddakdaegi.domain.order.service;

import com.example.ddakdaegi.domain.member.entity.Member;
import com.example.ddakdaegi.domain.member.repository.MemberRepository;
import com.example.ddakdaegi.domain.order.dto.response.OrderDetailResponse;
import com.example.ddakdaegi.domain.order.dto.response.OrderResponse;
import com.example.ddakdaegi.domain.order.dto.response.StockResponse;
import com.example.ddakdaegi.domain.order.entity.Order;
import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import com.example.ddakdaegi.domain.order.repository.OrderPromotionProductRepository;
import com.example.ddakdaegi.domain.order.repository.OrderRepository;
import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import com.example.ddakdaegi.global.common.dto.AuthUser;
import com.example.ddakdaegi.global.common.exception.BaseException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.NOT_FOUND_MEMBER;
import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.NOT_FOUND_ORDER;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderPromotionProductRepository orderPromotionProductRepository;
	private final MemberRepository memberRepository;


	@Transactional
	public OrderResponse createOrder(AuthUser authUser, StockResponse stockResponse) {

		Member getMember = memberRepository.findById(authUser.getId())
			.orElseThrow(() -> new BaseException(NOT_FOUND_MEMBER));

		Order newOrder = Order.of(getMember, stockResponse.getTotalPrice());
		orderRepository.save(newOrder);

		List<OrderPromotionProduct> orderPromotionProducts = stockResponse.getPromotionProducts().stream()
			.map(promotionProduct -> {
				Long quantity = stockResponse.getPromotionProductIdToQuantityMap().get(promotionProduct.getId());
				return OrderPromotionProduct.of(newOrder, promotionProduct, quantity);
			}).collect(Collectors.toList());

		orderPromotionProductRepository.saveAll(orderPromotionProducts);
		return OrderResponse.of(newOrder, orderPromotionProducts);
	}

	@Transactional(readOnly = true)
	public OrderDetailResponse getOrder(Long orderId, AuthUser authUser) {
		Member member = memberRepository.findById(authUser.getId())
			.orElseThrow(() -> new BaseException(NOT_FOUND_MEMBER));

		Order getOrder = orderRepository.findByOrderIdAndMemberId(orderId, authUser.getId())
			.orElseThrow(() -> new BaseException(NOT_FOUND_ORDER));

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
			.orElseThrow(() -> new BaseException(NOT_FOUND_ORDER));

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