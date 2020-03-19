package com.github.binlaniua.common.valid;

import com.github.binlaniua.common.context.RequestHolder;
import com.google.code.kaptcha.Constants;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Tkk
 */
public class CaptchaValidator implements ConstraintValidator<Captcha, String> {

    @Override
    public void initialize(final Captcha captcha) {
    }

    @Override
    public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {
        final HttpSession session = RequestHolder.get()
                                                 .getSession();
        final String key = (String) session.getAttribute(Constants.KAPTCHA_SESSION_KEY);
        session.removeAttribute(Constants.KAPTCHA_SESSION_KEY);
        return StringUtils.equalsIgnoreCase(key, s);
    }
}
