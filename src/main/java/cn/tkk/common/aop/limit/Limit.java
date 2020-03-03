package cn.tkk.common.aop.limit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Tkk
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {

    String key() default "";

    long timeout() default 5L;

    TimeUnit unit() default TimeUnit.SECONDS;

    String message() default "请忽重复提交";
}
