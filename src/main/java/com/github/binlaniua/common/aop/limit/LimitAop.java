package com.github.binlaniua.common.aop.limit;

import cn.tkk.common.aop.BaseAop;
import cn.tkk.common.exception.SystemException;
import cn.tkk.common.util.SpelHelper;
import com.github.binlaniua.common.exception.SystemException;
import com.github.binlaniua.common.util.SpelHelper;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Tkk
 */
@Component
@Aspect
public class LimitAop extends BaseAop {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Before("@annotation(cn.tkk.common.aop.limit.Limit)")
    void execute(final JoinPoint joinPoint) throws Throwable {

        //
        final Method method = this.getMethod(joinPoint);
        final Limit annotation = method.getAnnotation(Limit.class);

        //
        final String redisKey = method.getName() + (StringUtils.isBlank(annotation.key()) ? "" : SpelHelper.exec(annotation.key(), this.getArgs(joinPoint)));

        // 没值, 缓存一下
        if (this.redisTemplate.opsForValue()
                              .setIfAbsent(redisKey, "v")) {
            this.redisTemplate.expire(redisKey, annotation.timeout(), annotation.unit());
        }
        // 有值, 所有有人提交过了, 出错
        else {
            throw new SystemException("000007", annotation.message());
        }
    }
}
