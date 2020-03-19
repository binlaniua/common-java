package com.github.binlaniua.common.valid;

import com.github.binlaniua.common.util.SpelHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collections;

/**
 * Tkk
 */
public class SpelValidator implements ConstraintValidator<SpelValidate, Object> {

    private String spel;

    @Override
    public void initialize(final SpelValidate exists) {
        this.spel = exists.spel();
    }

    @Override
    public boolean isValid(final Object v, final ConstraintValidatorContext constraintValidatorContext) {
        return SpelHelper.exec(this.spel, Collections.singletonMap("value", v));
    }
}
