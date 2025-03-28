package com.example.ddakdaegi.domain.promotion.repository;

import com.example.ddakdaegi.domain.promotion.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

	@Query("select p.isActive from Promotion p where p.id = :promotionId")
	Boolean promotionIsActive(@Param("promotionId") Long promotionId);
}
