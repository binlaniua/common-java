package com.github.binlaniua.common.valid;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * Tkk
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private static final Pattern p = Pattern.compile("1{10}");

    @Override
    public void initialize(final Phone geo) {
    }

    @Override
    public boolean isValid(final String v, final ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isBlank(v)) {
            return true;
        }
        return p.matcher(v)
                .find();
    }
}
