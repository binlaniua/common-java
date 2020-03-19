package com.github.binlaniua.common.util;

import java.math.BigDecimal;

/**
 *  Tkk
 */
public class MathHelper {

    public static double toFixed(Double value, int size) {
        return BigDecimal.valueOf(value == null ? 0 : value).setScale(size, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double toFixed(Double value){
        return toFixed(value, 2);
    }
}
