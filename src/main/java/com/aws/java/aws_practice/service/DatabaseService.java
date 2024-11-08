package com.aws.java.aws_practice.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class DatabaseService {

    @Autowired
    private RedisLockService redisLockService;

    public String accessDatabase(String key) {
    	boolean capturedLock = false;
        try {
            String lockId = redisLockService.acquireLockWithRetry();

            if (lockId != null) {
                try {
                    log.info("Lock acquired. Accessing database...");
                    performDatabaseOperations(key);
                } finally {
                    redisLockService.releaseLock(lockId);
                    log.info("Lock released.");
                    capturedLock = true;
                }
            } else {
            	log.error("Could not acquire lock after maximum retries. Request is being ignored.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting to acquire lock.");
        }
        
        return capturedLock ? "Acquired lock" : "Could not get the lock";
    }

    private void performDatabaseOperations(String key) {
        // Simulate database access
        try {
        	if(key.equalsIgnoreCase("1min")) {// Simulate database processing time
        		Thread.sleep(60000);
        	}else {
        		Thread.sleep(3000);  
        	}
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log.error("Database operations completed.");
    }
}
