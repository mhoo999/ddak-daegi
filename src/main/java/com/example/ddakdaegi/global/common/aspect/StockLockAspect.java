package com.example.ddakdaegi.global.common.aspect;

import com.example.ddakdaegi.global.common.annotation.RedissonLocked;
import com.example.ddakdaegi.global.common.exception.BaseException;
import com.example.ddakdaegi.global.common.exception.enums.ErrorCode;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class StockLockAspect {

	private final RedissonClient redissonClient;

	@Around("@annotation(com.example.ddakdaegi.global.common.annotation.RedissonLocked)")
	public Object lockAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		log.info("AOP Lock 로직 시작");

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		RedissonLocked annotation = method.getAnnotation(RedissonLocked.class);

		String key = annotation.key();
		RLock rLock = redissonClient.getFairLock(key);
		try {
			log.info("Lock 획득 준비: {} - Thread: {}", key, Thread.currentThread().getName());
			boolean available = rLock.tryLock(annotation.waitTime(), annotation.leaseTime(), annotation.timeUnit());
			if (!available) {
				log.error("Lock 획득 실패: {} - Thread: {}", key, Thread.currentThread().getName());
				throw new BaseException(ErrorCode.LOCK_ACQUISITION_FAILED);
			}
			log.info("Lock 획득 성공: {} - Thread: {} - Time: {}",
				key, Thread.currentThread().getName(), System.currentTimeMillis());
			
			return joinPoint.proceed();
		} catch (InterruptedException e) {
			log.error("[InterruptedException]: ", e.fillInStackTrace());
			throw new BaseException(ErrorCode.LOCK_ACQUISITION_INTERRUPTED);
		} finally {
			try {
				if (rLock.isHeldByCurrentThread()) {
					rLock.unlock();
					log.info("Lock 해제 성공");
				}
			} catch (Exception e) {
				log.error("Lock 해제 중 예외 발생: ", e.fillInStackTrace());
			}
			log.info("AOP Lock 로직 끝");
		}
	}
}
