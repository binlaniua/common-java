package cn.tkk.common.jpa.query.config;

/**
 * Created by Tkk on 2018/7/21.
 */
public enum QueryConidtonType {

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

    // 右%
    rightLike,

    //
    notLike,

    //
    betweenDate,

    notNull, in, leDate, ltDate, equalDate;
}
