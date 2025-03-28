package com.example.ddakdaegi.global.scheduler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PromotionSchedulerService {

	private final String START_KEY = "promotion:start";
	private final String END_KEY = "promotion:end";

	private final RedisTemplate<String, String> redisTemplate;

	public void schedulePromotion(Long promotionId, LocalDateTime time, boolean activate) {
		String key = activate ? START_KEY : END_KEY;
		long score = time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		redisTemplate.opsForZSet().add(key, promotionId.toString(), score);
	}
}
