package com.example.ddakdaegi.domain.order.repository;

import com.example.ddakdaegi.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
