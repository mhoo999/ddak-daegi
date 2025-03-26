package com.example.ddakdaegi.domain.product.repository;

import com.example.ddakdaegi.domain.product.dto.response.ProductResponse;
import com.example.ddakdaegi.domain.product.entity.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findByDeletedAtNull(Pageable pageable); // deleteAt이 null 인 즉 product 값이 존재하는경우

}
