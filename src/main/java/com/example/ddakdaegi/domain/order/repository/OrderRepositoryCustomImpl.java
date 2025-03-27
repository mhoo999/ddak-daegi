package com.example.ddakdaegi.domain.order.repository;

import com.example.ddakdaegi.domain.order.dto.response.OrderPromotionProductDto;
import com.example.ddakdaegi.domain.order.dto.response.OrderResponse;
import com.example.ddakdaegi.domain.order.entity.Order;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionProductDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.example.ddakdaegi.domain.order.entity.QOrder.order;
import static com.example.ddakdaegi.domain.order.entity.QOrderPromotionProduct.orderPromotionProduct;

@Service
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<OrderResponse> findOrders(Long memberId, Pageable pageable) {
		List<Order> orders = queryFactory
			.selectFrom(order)
			.where(order.member.id.eq(memberId))
			.orderBy(order.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long totalCount = queryFactory
			.select(order.count())
			.from(order)
			.where(order.member.id.eq(memberId))
			.fetchOne();

		List<OrderResponse> content = orders.stream()
			.map(o -> {
				List<OrderPromotionProductDto> orderPromotionProductDtos = queryFactory
					.select(
						Projections.constructor(
							OrderPromotionProductDto.class,
							orderPromotionProduct.id,
							Projections.constructor(
								PromotionProductDto.class,
								orderPromotionProduct.promotionProduct.id,
								orderPromotionProduct.promotionProduct.product.name,
								orderPromotionProduct.promotionProduct.product.image,
								orderPromotionProduct.promotionProduct.product.price
							),
							orderPromotionProduct.quantity
						)
					)
					.from(orderPromotionProduct)
					.where(orderPromotionProduct.order.id.eq(o.getId()))
					.fetch();

				return OrderResponse.ofDto(o, orderPromotionProductDtos);
			}).collect(Collectors.toList());

		return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0);
	}

}
