package com.github.binlaniua.common.aop;

import javassist.NotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tkk
 */
public abstract class BaseAop {

    private static final Map<Method, String[]> methodParamsCache = new ConcurrentHashMap<>();

    /**
     * @param point
     * @param clazz
     * @param <T>
     * @return
     */
    protected <T> T getAnnotation(final JoinPoint point, final Class clazz) throws NoSuchMethodException {
        final Method method = this.getMethod(point);
        return (T) method.getAnnotation(clazz);
    }

    /**
     * @param point
     * @return
     * @throws NoSuchMethodException
     */
    protected Method getMethod(final JoinPoint point) throws NoSuchMethodException {
        final MethodSignature signature = (MethodSignature) point.getSignature();
        return point.getTarget()
                    .getClass()
                    .getMethod(signature.getName(), signature.getParameterTypes());
    }


    /**
     * @param point
     * @return
     */
    protected String getMethodName(final JoinPoint point) throws NoSuchMethodException {
        return this.getMethod(point)
                   .getName();
    }

    /**
     * 获取方法的参数和参数值
     *
     * @param point
     * @return
     * @throws NotFoundException
     */
    protected Map<String, Object> getArgs(final JoinPoint point) {
        final MethodSignature signature = (MethodSignature) point.getSignature();
        final String[] parameterNames = methodParamsCache.computeIfAbsent(signature.getMethod(), (m) -> {
            final LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
            return u.getParameterNames(signature.getMethod());
        });
        //
        final Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            map.put(parameterNames[i], point.getArgs()[i]); //paramNames即参数名
        }
        return map;
    }
}
