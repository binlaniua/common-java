package com.github.binlaniua.common.jpa.query.config;

/**
 * Created by Tkk on 2018/7/21.
 */
public enum QueryConditionType {

    // 等于
    equal,

    // 大于
    gt,
    gtDate,

    // 大于等于
    ge,
    geDate,

    // 小于
    lt,

    // 小于等于
    le,

    // 不等于, 少用。。负向查询性能底下
    notEqual,

    //
    like,

    // 强制匹配, 用于自定义 converter
    likeForce,

    // 右%
    rightLike,

    //
    notLike,

    //
    betweenDate,

    notNull, in, leDate, ltDate, equalDate;
}
