package com.github.binlaniua.common.aop.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 *  Tkk
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    String key() default "";

    long timeout() default -1;

    TimeUnit unit() default TimeUnit.SECONDS;

    String message() default "请稍候重试";
}
