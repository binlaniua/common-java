package com.github.binlaniua.common.valid;

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
@Constraint(validatedBy = CaptchaValidator.class)
public @interface Captcha {
    String message() default "图形验证码错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
