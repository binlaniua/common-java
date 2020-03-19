package com.github.binlaniua.common.aop.lock;

import cn.tkk.common.aop.BaseAop;
import cn.tkk.common.util.SpelHelper;
import com.github.binlaniua.common.util.SpelHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Tkk
 */
@Slf4j
@Component
@Aspect
public class LockAop extends BaseAop {

    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(cn.tkk.common.aop.lock.Lock)")
    Object execute(final ProceedingJoinPoint joinPoint) throws Throwable {
        //
        final Method method = this.getMethod(joinPoint);
        final Lock annotation = method.getAnnotation(Lock.class);

        //
        final Map<String, Object> args = this.getArgs(joinPoint);

        //
        final String redisKey = method.getName() + "_" + SpelHelper.exec(annotation.key(), args);
        final RLock lock = this.redissonClient.getLock(redisKey);
        try {
            if (lock == null) {
                return joinPoint.proceed();
            }
            try {
                if (annotation.timeout() != -1) {
                    lock.lock(annotation.timeout(), annotation.unit());
                } else {
                    lock.lock();
                }
            } catch (final Exception e) {
                log.error(redisKey, e);
            }
            return joinPoint.proceed();
        } finally {
            if (lock != null) {
                try {
                    lock.unlock();
                } catch (final Exception e) {
                }
            }
        }
    }
}
