package com.iftrue.delivery.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryManagerRoundRobinCounter {

    private final StringRedisTemplate redisTemplate;

    private static final String HUB_KEY =
            "delivery:manager:round-robin:hub";

    public long nextHub() {
        Long value = redisTemplate
                .opsForValue()
                .increment(HUB_KEY);

        if (value == null) {
            throw new IllegalStateException("Redis counter increment 실패");
        }

        return value;
    }

    public long nextCompany(UUID hubId) {
        String key =
                "delivery:manager:round-robin:company:" + hubId;

        Long value = redisTemplate
                .opsForValue()
                .increment(key);

        if (value == null) {
            throw new IllegalStateException("Redis counter increment 실패");
        }

        return value;
    }
}
