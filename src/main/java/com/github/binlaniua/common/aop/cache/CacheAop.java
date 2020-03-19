package com.github.binlaniua.common.aop.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.binlaniua.common.aop.BaseAop;
import com.github.binlaniua.common.util.SpelHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * Tkk
 */
@Slf4j
@Component
@Aspect
public class CacheAop extends BaseAop implements InitializingBean {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private DefaultRedisScript luaRemoveAllScript;

    /**
     * 缓存cache
     *
     * @param proceedingJoinPoint
     * @throws Throwable
     */
    @Around("@annotation(com.github.binlaniua.common.aop.cache.Cacheable)")
    Object cacheable(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            // 1. 获取方法
            final Method method = this.getMethod(proceedingJoinPoint);
            final Cacheable annotation = method.getAnnotation(Cacheable.class);

            // 2. 获取参数, 运行spel, 获取key
            final Map<String, Object> args = this.getArgs(proceedingJoinPoint);
            final String redisKey = annotation.value() + SpelHelper.exec(annotation.key(), args);
            final BoundValueOperations<String, String> value = this.redisTemplate.boundValueOps(redisKey);

            // 3. 有值
            final String result = value.get();
            if (StringUtils.isNotBlank(result)) {
                return this.objectMapper.readValue(result, method.getReturnType());
            }

            // 4.
            final Object proceed = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
            if (proceed == null) {
                return null;
            }

            // 5. 判断是否要存
            if (StringUtils.isNotBlank(annotation.exclude())) {
                args.put("result", proceed);
                final Boolean isNotSave = SpelHelper.exec(annotation.exclude(), args);
                if (isNotSave) {
                    return proceed;
                }
            }

            // 6. 开始存
            final String jsonString = this.objectMapper.writeValueAsString(proceed);
            if (annotation.timeout() != -1L) {
                value.set(jsonString, annotation.timeout(), annotation.unit());
            } else {
                value.set(jsonString);
            }

            // 7. 放入一个list, 后面可以删除
            if (StringUtils.isNotBlank(annotation.value())) {
                this.redisTemplate.boundListOps(annotation.value())
                                  .leftPush(redisKey);
            }
            return proceed;
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return proceedingJoinPoint.proceed();
        }
    }

    /**
     * 清空cache
     *
     * @param joinPoint
     * @throws Throwable
     */
    @Before("@annotation(com.github.binlaniua.common.aop.cache.CacheEvict)")
    void cacheEvict(final JoinPoint joinPoint) throws Throwable {
        final CacheEvict annotation = this.getAnnotation(joinPoint, CacheEvict.class);
        // 清空所有
        if (annotation.allEntries()) {
            if (annotation.value().length > 0) {
                for (final String v : annotation.value()) {
                    this.redisTemplate.execute(this.luaRemoveAllScript, Collections.singletonList(v));
                }
            }
        }
        // 清空单个
        else {
            final String v = annotation.value()[0];
            final Map<String, Object> args = this.getArgs(joinPoint);
            final String redisKey = v + SpelHelper.exec(annotation.key(), args);
            this.redisTemplate.delete(redisKey);
            this.redisTemplate.boundListOps(v)
                              .remove(1, redisKey);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.luaRemoveAllScript = new DefaultRedisScript<>();
        this.luaRemoveAllScript.setScriptText("local size = redis.call('llen', KEYS[1])\n" +
                "local vs = redis.call('lrange', KEYS[1], 0, size)\n" +
                "for x,y in ipairs(vs) do\n" +
                "        redis.call('del', y)\n" +
                "end\n" +
                "redis.call('ltrim', KEYS[1], size, -1)");
    }
}
