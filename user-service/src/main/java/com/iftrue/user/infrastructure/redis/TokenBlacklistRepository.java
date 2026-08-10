package com.iftrue.user.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TokenBlacklistRepository {

    private static final String PREFIX = "blacklist:";

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String accessToken, long expirationMillis) {
        if (expirationMillis <= 0) {
            return;
        }

        redisTemplate.opsForValue().set(
                PREFIX + accessToken,
                "logout",
                expirationMillis,
                TimeUnit.MILLISECONDS
        );
    }


}
