package com.github.binlaniua.common.util;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tkk
 */
public class SpelHelper {

    private final static ConcurrentHashMap<String, Expression> m = new ConcurrentHashMap<>();

    /**
     * 动态执行
     *
     * @param exp
     * @param values
     * @param <T>
     * @return
     */
    public static <T> T exec(String exp, Map<String, Object> values) {
        Expression expression = m.computeIfAbsent(exp, k -> {
            return new SpelExpressionParser().parseExpression(exp);
        });
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariables(values);
        return (T) expression.getValue(context);
    }
}
