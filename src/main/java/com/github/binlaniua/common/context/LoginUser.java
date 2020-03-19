package com.github.binlaniua.common.context;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;

/**
 * Tkk
 */
public interface LoginUser<T> {

    /**
     * Id
     *
     * @return
     */
    T getId();

    /**
     * 用户名
     *
     * @return
     */
    String getName();

    /**
     * @param claims
     */
    void fromJwt(Claims claims);

    /**
     * @param claims
     */
    void toJwt(JwtBuilder claims);
}
