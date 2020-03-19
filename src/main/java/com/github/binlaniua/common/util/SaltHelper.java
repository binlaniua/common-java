package com.github.binlaniua.common.util;


import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

/**
 * Tkk
 */
public class SaltHelper {

    /**
     * 比较相等
     *
     * @param src
     * @param give
     * @param salt
     * @return
     */
    public static boolean isEquals(final String src, final String give, final String salt) {
        return src.equals(exec(give, salt));
    }

    /**
     * @param give
     * @param salt
     * @return
     */
    public static String exec(final String give, final String salt) {
        return DigestUtils.md5Hex(give + DigestUtils.md5Hex(salt));
    }

    /**
     * @return
     */
    public static String salt() {
        return DigestUtils.md5Hex(UUID.randomUUID()
                                      .toString());
    }
}
