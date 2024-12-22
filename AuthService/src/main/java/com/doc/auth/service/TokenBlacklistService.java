package com.doc.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {
    
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "token:blacklist:access:";
    private static final String REFRESH_TOKEN_BLACKLIST_PREFIX = "token:blacklist:refresh:";
    
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public TokenBlacklistService(@Qualifier("redisTemplate") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addToBlacklist(String token, String type, long ttl) {
        String prefix = type.equals("ACCESS") ? ACCESS_TOKEN_BLACKLIST_PREFIX : REFRESH_TOKEN_BLACKLIST_PREFIX;
        redisTemplate.opsForValue().set(prefix + token, "1", ttl, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token, String type) {
        String prefix = type.equals("ACCESS") ? ACCESS_TOKEN_BLACKLIST_PREFIX : REFRESH_TOKEN_BLACKLIST_PREFIX;
        return Boolean.TRUE.equals(redisTemplate.hasKey(prefix + token));
    }
} 