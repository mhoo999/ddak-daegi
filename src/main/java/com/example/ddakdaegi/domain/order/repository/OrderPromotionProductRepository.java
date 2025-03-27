package com.example.ddakdaegi.domain.order.repository;

import com.example.ddakdaegi.domain.order.entity.OrderPromotionProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderPromotionProductRepository extends JpaRepository<OrderPromotionProduct, Long> {

	@Query("select opp from OrderPromotionProduct opp where opp.order.id in :orderId")
	List<OrderPromotionProduct> findByOrderId(Long orderId);
}
