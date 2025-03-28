package com.example.ddakdaegi.global.common.aspect;

import com.example.ddakdaegi.domain.order.dto.request.PromotionProductRequest;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import static com.example.ddakdaegi.global.common.exception.enums.ErrorCode.LOCK_ACQUISITION_FAILED;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class StockLockAspect {

	private final RedissonClient redissonClient;

	@Around("@annotation(com.example.ddakdaegi.global.common.annotation.Locked)")
	public Object lockAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("AOP Lock 로직 시작");

		Object[] args = joinPoint.getArgs();
		Long promotionId = (Long) args[0];
		List<PromotionProductRequest> requests = (List<PromotionProductRequest>) args[1];

		List<String> keys = requests.stream()
			.map(PromotionProductRequest::getPromotionProductId)
			.sorted()
			.map(id -> "PromotionProduct:" + id + ":stock")
			.toList();

		List<RLock> acquiredLocks = new ArrayList<>();
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

			return joinPoint.proceed(new Object[]{promotionId, requests});
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
			log.info("AOP Lock 로직 끝");
		}
	}

}
