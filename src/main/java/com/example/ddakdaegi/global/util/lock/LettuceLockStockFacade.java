package com.example.ddakdaegi.global.util.lock;

import com.example.ddakdaegi.domain.order.dto.request.PromotionProductRequest;
import com.example.ddakdaegi.domain.order.dto.response.StockResponse;
import com.example.ddakdaegi.domain.order.service.StockService;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {

	private final StockService stockService;
	private final RedisLockRepository redisLockRepository;

	public StockResponse lockAndDecreaseStock(Long promotionId, List<PromotionProductRequest> requests) {
		List<String> keys = requests.stream()
			.map(PromotionProductRequest::getPromotionProductId)
			.sorted()
			.map(id -> "PromotionProduct:" + id + ":stock")
			.toList();

		List<String> acquiredKeys = new ArrayList<>();

		try {
			for (String key : keys) {
				log.info("Lock 획득 준비: {} - Thread: {}", key, Thread.currentThread().getName());
				while (!redisLockRepository.lock(key)) {
					log.info("Lock 획득 대기 중: {} - Thread: {}", key, Thread.currentThread().getName());
					Thread.sleep(200);
				}
				log.info("Lock 획득 성공: {} - Thread: {}", key, Thread.currentThread().getName());
				acquiredKeys.add(key);
			}

			return stockService.decreaseStockAndCalculateTotalPrice(promotionId, requests);
		} catch (InterruptedException e) {
			log.error("[InterruptedException]: ", e);
			throw new BaseException(ErrorCode.LOCK_ACQUISITION_INTERRUPTED);
		} finally {
			// 획득한 모든 락 해제
			acquiredKeys.forEach(redisLockRepository::unlock);
		}
	}

}
