package com.example.ddakdaegi.domain.promotion.repository;

import com.example.ddakdaegi.domain.promotion.entity.PromotionProduct;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<PromotionProduct> findAllByIdIn(Collection<Long> ids);
}
