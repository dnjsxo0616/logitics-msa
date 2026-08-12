package com.iftrue.user.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    @Value("${service.jwt.refresh-expiration}")
    private long refreshExpiration;

    public void save(
            UUID userId,
            String refreshToken
    ) {
        String key = KEY_PREFIX + userId;

        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                refreshExpiration,
                TimeUnit.MILLISECONDS
        );
    }

    public String findByUserId(UUID userId) {
        return redisTemplate.opsForValue()
                .get(KEY_PREFIX + userId);
    }

    public void deleteByUserId(UUID userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}