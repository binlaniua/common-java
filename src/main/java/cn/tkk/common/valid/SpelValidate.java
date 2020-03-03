package cn.tkk.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Tkk
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SpelValidator.class)
public @interface SpelValidate {
    String message() default "数据已经存在";

    String spel();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
