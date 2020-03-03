package cn.tkk.common.aop.cache;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 *  Tkk
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Cacheable {

    String value() default "";

    String key() default "";

    long timeout() default -1L;

    String exclude() default "";

    TimeUnit unit() default TimeUnit.SECONDS;
}
