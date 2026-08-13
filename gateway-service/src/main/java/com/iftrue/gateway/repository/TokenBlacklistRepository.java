package com.iftrue.gateway.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class TokenBlacklistRepository {

    private static final String PREFIX = "blacklist:";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public Mono<Boolean> exists(String token) {
        return redisTemplate.hasKey(PREFIX + token);
    }


}
