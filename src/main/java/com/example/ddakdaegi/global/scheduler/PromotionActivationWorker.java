package com.example.ddakdaegi.global.scheduler;

import com.example.ddakdaegi.domain.promotion.repository.PromotionRepository;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PromotionActivationWorker {

	private final String START_KEY = "promotion:start";
	private final String END_KEY = "promotion:end";

	private final RedisTemplate<String, String> redisTemplate;
	private final RedissonClient redissonClient;
	private final PromotionRepository promotionRepository;

	@Scheduled(fixedRate = 10_000) // 10초마다 실행
	@Transactional
	public void processScheduledPromotions() throws InterruptedException {
		processKey(START_KEY, true);
		processKey(END_KEY, false);
	}

	private void processKey(String key, boolean activate) throws InterruptedException {
		long now = System.currentTimeMillis();
		Set<String> readyIds = redisTemplate.opsForZSet().rangeByScore(key, 0, now);

		for (String promotionId : readyIds) {
			String lockKey = "lock:" + key + ":" + promotionId;
			RLock lock = redissonClient.getLock(lockKey);
			boolean gotLock = false;

			try {
				gotLock = lock.tryLock(1, 5, TimeUnit.SECONDS);
				if (gotLock) {
					promotionRepository.findById(Long.valueOf(promotionId)).ifPresent(promotion -> {
						promotion.setActive(activate);
					});

					redisTemplate.opsForZSet().remove(key, promotionId);
				}
			} finally {
				if (gotLock) lock.unlock();
			}
		}
	}
}
