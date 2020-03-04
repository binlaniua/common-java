package cn.tkk.common.valid;

import cn.hutool.core.convert.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Column;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tkk
 */
public class DBLengthValidator implements ConstraintValidator<DBLength, String> {

    private static final Pattern PATTERN = Pattern.compile("\\((\\d+)\\)\\s+comment\\s+'([^']+)'");

    @Data
    @AllArgsConstructor
    private static class NameAndLength {
        private Integer length;
        private String name;
    }

    private static final Map<DBLength, NameAndLength> cache = new ConcurrentHashMap<>();

    private NameAndLength nl;

    @Override
    public void initialize(final DBLength dbLength) {
        this.nl = cache.computeIfAbsent(dbLength, (d) -> {
            final Field field = ReflectionUtils.findField(dbLength.entity(), dbLength.column());
            final Column annotation = field.getAnnotation(Column.class);
            if (annotation == null) {
                return null;
            }
            final String s = annotation.columnDefinition();
            if (StringUtils.isNotBlank(s)) {
                final Matcher matcher = PATTERN.matcher(s);
                if (matcher.find()) {
                    return new NameAndLength(Convert.toInt(matcher.group(1)), matcher.group(2));
                }
            }
            return new NameAndLength(annotation.length(), "");
        });
    }

    @Override
    public boolean isValid(final String s, final ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        final boolean b = this.nl == null || s.length() <= this.nl.length;
        if (!b) {
            final String errorMessage = constraintValidatorContext.getDefaultConstraintMessageTemplate();
            if (StringUtils.isBlank(errorMessage)) {
                constraintValidatorContext.buildConstraintViolationWithTemplate(this.nl.name + "数据过长,最多输入" + this.nl.length + "个字符")
                                          .addConstraintViolation();
            }
        }
        return b;
    }
}
