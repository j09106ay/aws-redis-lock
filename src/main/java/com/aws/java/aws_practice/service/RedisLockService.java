package com.aws.java.aws_practice.service;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class RedisLockService {
	
	@Autowired
	RedisService redisService;

    private static final String LOCK_KEY = "dbAccessLock";
    private static final int MAX_RETRIES = 5;  // Maximum retry attempts
    private static final long RETRY_DELAY_MS = 1000;  // Delay between retries in milliseconds

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String acquireLockWithRetry() throws InterruptedException {
        String lockId = UUID.randomUUID().toString();
        int retries = 0;

        while (retries < MAX_RETRIES) {
            Boolean acquired = redisService.isLockAcquired(lockId, LOCK_KEY);

            if (acquired != null && acquired) {
                return lockId;  // Successfully acquired the lock
            }

            retries++;
            log.info("Lock not available, retrying... ( {}  /  {}  )",retries,MAX_RETRIES);
            Thread.sleep(RETRY_DELAY_MS);  // Wait before retrying
        }

        return null;  // Lock not acquired after max retries
    }

    public boolean releaseLock(String lockId) {
        String currentLockId = redisService.getString(LOCK_KEY);

        if (lockId.equals(currentLockId)) {
            redisTemplate.delete(LOCK_KEY);
            return true;
        }
        return false;
    }
}