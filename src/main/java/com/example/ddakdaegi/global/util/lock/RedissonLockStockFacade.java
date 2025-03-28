package com.example.ddakdaegi.global.util.lock;

import com.example.ddakdaegi.domain.order.dto.request.PromotionProductRequest;
import com.example.ddakdaegi.domain.order.dto.response.StockResponse;
import com.example.ddakdaegi.domain.order.service.StockService;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.LOCK_ACQUISITION_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {

	private final RedissonClient redissonClient;
	private final StockService stockService;

	public StockResponse lockAndDecreaseStock(Long promotionId, List<PromotionProductRequest> requests) {
		List<String> keys = requests.stream()
			.map(PromotionProductRequest::getPromotionProductId)
			.sorted()
			.map(id -> "PromotionProduct:" + id + ":stock")
			.toList();

		List<RLock> acquiredLocks = new ArrayList<>(); // 획득한 락을 저장
		try {
			for (String key : keys) {
				log.info("Lock 획득 준비: {} - Thread: {}", key, Thread.currentThread().getName());
				RLock fairLock = redissonClient.getFairLock(key);
				boolean available = fairLock.tryLock(1000, 100, TimeUnit.MILLISECONDS);
				if (!available) {
					log.info("Lock 획득 실패: {} - Thread: {}", key, Thread.currentThread().getName());
					throw new BaseException(LOCK_ACQUISITION_FAILED);
				}
				log.info("Lock 획득 성공: {} - Thread: {}", key, Thread.currentThread().getName());
				acquiredLocks.add(fairLock);
			}

			// 구매하려는 모든 상품에 대한 락을 얻은 뒤에 재고 감소 로직을 실행한다.
			return stockService.decreaseStockAndCalculateTotalPrice(promotionId, requests);
		} catch (InterruptedException e) {
			log.error("[InterruptedException]: ", e);
			throw new BaseException(ErrorCode.LOCK_ACQUISITION_INTERRUPTED);
		} finally {
			for (RLock acquiredLock : acquiredLocks) {
				try {
					acquiredLock.unlock();
				} catch (Exception e) {
					log.error("Lock 해제 중 예외 발생: ", e);
				}
			}

		}
	}

}
