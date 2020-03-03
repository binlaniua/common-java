package cn.tkk.common.aop.cache;

import java.lang.annotation.*;

/**
 *  Tkk
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheEvict {

    String[] value() default "";

    String key() default "";

    boolean allEntries() default false;
}
