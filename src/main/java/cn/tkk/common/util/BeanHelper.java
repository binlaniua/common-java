package cn.tkk.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import java.beans.PropertyDescriptor;
import java.util.LinkedList;
import java.util.List;

/**
 * Tkk
 */
public class BeanHelper {

    /**
     * 主要过滤 null 值
     *
     * @param oldValue
     * @param newValue
     */
    public static void copyProperties(Object oldValue, Object newValue) {
        BeanUtils.copyProperties(newValue, oldValue, getNullPropertyNames(newValue));
    }

    /**
     * 主要过滤 null 值
     *
     * @param oldValue
     * @param newValue
     */
    public static void copyProperties(Object oldValue, Object newValue, String... exclude) {
        BeanUtils.copyProperties(newValue, oldValue, getNullPropertyNames(newValue, exclude));
    }

    /**
     * 忽略为null的数据
     *
     * @param source
     * @return
     */
    public static String[] getNullPropertyNames(Object source, String... ignores) {
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(source);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();
        List<String> ignoreList = new LinkedList<>();
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() == null) {
                continue;
            }
            Object srcValue = beanWrapper.getPropertyValue(pd.getName());
            if (srcValue == null) {
                ignoreList.add(pd.getName());
            }
        }
        String[] result = ignoreList.toArray(new String[ignoreList.size()]);
        return ignores == null ? result : ArrayUtils.addAll(result, ignores);
    }

}
