package com.example.ddakdaegi.domain.order.repository;

import com.example.ddakdaegi.domain.order.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

	Page<OrderResponse> findOrders(Long memberId, Pageable pageable);
}
