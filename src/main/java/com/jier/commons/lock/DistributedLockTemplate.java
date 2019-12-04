package com.jier.commons.lock;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 基于redisson的注解锁
 */
@Component
public class DistributedLockTemplate {

    long DEFAULT_WAIT_TIME = 60L;
    long DEFAULT_TIMEOUT = 60L;

    TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    @Autowired
    private RedissonClient redissonClient;

    public DistributedLockTemplate() {
    }

    public <T> T lock(DistributedLockCallback<T> callback, boolean fairLock) {
        return lock(callback, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT, fairLock);
    }

    public <T> T lock(DistributedLockCallback<T> callback, long leaseTime, TimeUnit timeUnit, boolean fairLock) {
        RLock lock = getLock(callback.getLockName(), fairLock);
        try {
            lock.lock(leaseTime, timeUnit);
            return callback.process();
        } finally {
            if (lock != null && lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    public <T> T tryLock(DistributedLockCallback<T> callback, boolean fairLock) {
        return tryLock(callback, DEFAULT_WAIT_TIME, DEFAULT_TIMEOUT, DEFAULT_TIME_UNIT, fairLock);
    }

    public <T> T tryLock(DistributedLockCallback<T> callback, long waitTime, long leaseTime, TimeUnit timeUnit,
            boolean fairLock) {
        RLock lock = getLock(callback.getLockName(), fairLock);
        try {
            if (lock.tryLock(waitTime, leaseTime, timeUnit)) {
                return callback.process();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException (e);
        } finally {
            if (lock != null && lock.isLocked()) {
                lock.unlock();
            }
        }
        return null;
    }

    private RLock getLock(String lockName, boolean fairLock) {
        RLock lock;
        if (fairLock) {
            lock = redissonClient.getFairLock(lockName);
        } else {
            lock = redissonClient.getLock(lockName);
        }
        return lock;
    }

}
