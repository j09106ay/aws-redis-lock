package com.aws.java.aws_practice.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final Duration LOCK_EXPIRY = Duration.ofSeconds(10);  // Adjust lock expiration time as needed


    public void saveString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public String getString(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    
    public boolean isLockAcquired(String lockId, String lockKey) {
    	return redisTemplate.opsForValue().setIfAbsent(lockKey, lockId, LOCK_EXPIRY);
    }
}
