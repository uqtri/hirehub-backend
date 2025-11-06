package org.example.hirehub.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String formatKey(String feature, String id, String name) {
        return String.format("%s:%s:%s", feature, id, name);
    }
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    public Long addRefreshToken(String userIdEmail, String refreshToken) {
        String key = formatKey("auth", userIdEmail, "refresh_token");

        Long added = stringRedisTemplate.opsForSet().add(key, refreshToken);

        final long THIRTY_DAYS = 30;

        stringRedisTemplate.expire(key, THIRTY_DAYS, TimeUnit.DAYS);

        return added;


    }
    public boolean isExistRefreshToken(String userIdEmail, String refreshToken) {

        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(formatKey("auth", userIdEmail, "refresh_token"), refreshToken));
    }
    public Long removeRefreshToken(String userIdEmail, String refreshToken) {
        if(refreshToken == null)
            return -1L;
        return stringRedisTemplate.opsForSet().remove(formatKey("auth", userIdEmail, "refresh_token"), refreshToken);
    }
}
