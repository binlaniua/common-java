package com.github.binlaniua.common.jwt;

import com.github.binlaniua.common.context.LoginUser;
import com.github.binlaniua.common.exception.SystemException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Tkk
 */
@Component
public class JwtUtils implements ApplicationContextAware {

    private final static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

    private static StringRedisTemplate redisTemplate;


    public static enum JwtType {
        Boss(DigestUtils.md5Hex("boss"), null),
        Api(DigestUtils.md5Hex("api"), 8L),;
        private final String key;
        private final Long timeout;

        JwtType(final String key, final Long timeout) {
            this.key = key;
            this.timeout = timeout; //小时
        }
    }

    /**
     * @param jwtToken
     * @return
     */
    public static void decode(final JwtType jwtType, final String jwtToken, final LoginUser loginUser) {
        try {
            if (StringUtils.isBlank(jwtToken)) {
                throw new RuntimeException();
            }
            //
            final Claims claimsJws = Jwts.parser()
                                         .setSigningKey(jwtType.key)
                                         .parseClaimsJws(jwtToken)
                                         .getBody();
            loginUser.fromJwt(claimsJws);

            //
            checkIsTimeout(loginUser.getId(), jwtType);
        } catch (final Exception e) {
            throw new SystemException("000002");
        }
    }

    /**
     * @param user
     * @return
     */
    public static String encode(final JwtType jwtType, final LoginUser user) {
        //
        final JwtBuilder builder = Jwts.builder()
                                       .claim("extra", RandomStringUtils.randomNumeric(16))
                                       .setIssuedAt(new Date());
        user.toJwt(builder);

        //
        final String jwtBuilder = builder.signWith(signatureAlgorithm, jwtType.key)
                                         .compact();

        //
        extendedTimeout(user.getId(), jwtType);
        return jwtBuilder;
    }

    /**
     * @param id
     */
    public static void delete(final Serializable id) {
        redisTemplate.delete(id.toString());
    }

    /**
     * @param idList
     */
    public static void delete(final List<? extends Serializable> idList) {
        redisTemplate.delete(idList.stream()
                                   .map(Object::toString)
                                   .collect(Collectors.toList()));
    }

    /**
     * @param id
     * @param jwtType
     */
    private static void extendedTimeout(final Object id, final JwtType jwtType) {
        if (jwtType.timeout == null || id == null) {
            return;
        }
        redisTemplate.boundValueOps(id.toString())
                     .set("", jwtType.timeout, TimeUnit.HOURS);
    }

    /**
     * @param id
     * @param jwtType
     */
    private static void checkIsTimeout(final Object id, final JwtType jwtType) {
        if (jwtType.timeout == null || id == null) {
            return;
        }
        if (redisTemplate.hasKey(id.toString())) {
            redisTemplate.expire(id.toString(), jwtType.timeout, TimeUnit.HOURS);
            return;
        }
        throw new SystemException("000002");
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        JwtUtils.redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
    }
}