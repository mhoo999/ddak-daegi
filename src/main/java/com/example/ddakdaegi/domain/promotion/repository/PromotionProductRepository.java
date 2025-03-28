package com.example.ddakdaegi.domain.promotion.repository;

import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

	List<PromotionProduct> findAllByIdIn(Collection<Long> ids);
}
