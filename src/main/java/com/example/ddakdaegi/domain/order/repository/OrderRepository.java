package com.example.ddakdaegi.domain.order.repository;

import com.example.ddakdaegi.domain.order.entity.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

	@Query("select o from Order o where o.id = :orderId and o.member.id = :memberId")
	Optional<Order> findByOrderIdAndMemberId(@Param("orderId") Long orderId, @Param("memberId") Long memberId);
}
