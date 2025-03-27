package com.example.ddakdaegi.domain.order.repository;

import com.example.ddakdaegi.domain.order.dto.response.OrderPromotionProductDto;
import com.example.ddakdaegi.domain.order.dto.response.OrderResponse;
import com.example.ddakdaegi.domain.promotion.dto.response.PromotionProductDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.example.ddakdaegi.domain.image.entity.QImage.image;
import static com.example.ddakdaegi.domain.order.entity.QOrder.order;
import static com.example.ddakdaegi.domain.order.entity.QOrderPromotionProduct.orderPromotionProduct;
import static com.example.ddakdaegi.domain.product.entity.QProduct.product;
import static com.example.ddakdaegi.domain.promotion.entity.QPromotionProduct.promotionProduct;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<OrderResponse> findOrders(Long memberId, Pageable pageable) {
		List<OrderResponse> content = queryFactory
			.select(
				Projections.constructor(
					OrderResponse.class,
					order.id,
					order.totalPrice,
					order.status,
					order.createdAt,
					Projections.list(
						Projections.constructor(
							OrderPromotionProductDto.class,
							orderPromotionProduct.id,
							Projections.constructor(
								PromotionProductDto.class,
								promotionProduct.id,
								product.name,
								image.imageUrl,
								promotionProduct.price
							),
							orderPromotionProduct.quantity
						)
					)
				)
			)
			.from(order)
			.join(orderPromotionProduct).on(order.id.eq(orderPromotionProduct.order.id))
			.join(orderPromotionProduct.promotionProduct, promotionProduct)
			.join(promotionProduct.product, product)
			.join(product.image, image)
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

		return new PageImpl<>(content, pageable, totalCount != null ? totalCount : 0);
	}

}
