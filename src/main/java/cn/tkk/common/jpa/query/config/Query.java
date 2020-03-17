package cn.tkk.common.jpa.query.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Tkk on 2018/7/21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Query {

    /**
     * 数据库字段
     *
     * @return
     */
    String column() default "";


    /**
     * 查询条件
     *
     * @return
     */
    QueryConditionType type() default QueryConditionType.equal;

    /**
     * Join条件
     *
     * @return
     */
    QueryJoinType join() default QueryJoinType.None;

    /**
     * object是否可以为null
     *
     * @return
     */
    boolean nullable() default false;

    /**
     * 字符串是否可为空
     *
     * @return
     */
    boolean blankable() default false;
}
