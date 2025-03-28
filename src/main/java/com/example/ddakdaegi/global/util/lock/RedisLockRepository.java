package com.example.ddakdaegi.global.util.lock;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisLockRepository {

	private final RedisTemplate<String, String> redisTemplate;

	public Boolean lock(Object key) {
		return redisTemplate
			.opsForValue()
			.setIfAbsent(key.toString(), "lock", Duration.ofMillis(3000));
	}

	public void unlock(Object key) {
		redisTemplate.delete(key.toString());
	}

}
