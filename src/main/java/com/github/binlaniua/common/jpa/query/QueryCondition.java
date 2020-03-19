package com.github.binlaniua.common.jpa.query;


import com.github.binlaniua.common.jpa.query.value.QueryItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Kun Tang on 2018/10/4.
 */
@Slf4j
public class QueryCondition {

    /**
     * 转成条件
     *
     * @param queryItem
     * @param cb
     * @param expression
     * @return
     */
    public static Predicate toCondition(final QueryItem queryItem, final CriteriaBuilder cb, final CriteriaQuery<?> criteriaQuery, final Expression expression) {
        final Object value = queryItem.getValue();
        switch (queryItem.getType()) {
            // in 查询
            case in:
                final List list = (List) value;
                if (list.isEmpty()) {
                    return null;
                }
                final CriteriaBuilder.In in = cb.in(expression);
                for (final Object o : list) {
                    in.value(o);
                }
                return in;

            // equal
            case equal:
                return cb.equal(expression, value);

            // equal date 时间相同, 得格式化
            case equalDate:
//                return cb.equal(cb.function("date_format", String.class, expression, cb.literal("%Y%m%d")),
//                        cb.function("date_format", String.class, cb.literal(value), cb.literal("%Y%m%d")));
                return cb.equal(expression, DateUtils.truncate((Date) value, Calendar.DATE));
            // like
            case like:
                return cb.like(expression, "%" + value + "%");

            //
            case likeForce:
                return cb.like(expression.as(String.class), "%" + value + "%");

            // 大于
            case gt:
                return cb.gt(expression, (Number) value);

            // 小于
            case lt:
                return cb.lt(expression, (Number) value);

            // 大于等于
            case ge:
                return cb.ge(expression, (Number) value);

            // 大于等于时间
            case gtDate:
                return cb.greaterThanOrEqualTo(expression, (Date) value);

            // 大于时间
            case geDate:
                return cb.greaterThan(expression, (Date) value);

            // 小于时间
            case leDate:
                return cb.lessThan(expression, (Date) value);

            // 小于等于时间
            case ltDate:
                return cb.lessThanOrEqualTo(expression, (Date) value);

            // 小于
            case le:
                return cb.le(expression, (Number) value);

            // 不等于
            case notEqual:
                return cb.notEqual(expression, value);

            // 不相似
            case notLike:
                return cb.notLike(expression, "%" + value + "%");

            //
            case rightLike:
                return cb.like(expression, value + "%");

            //
            case betweenDate:
                final Date[] timeRange = (Date[]) value;
                if (timeRange.length == 2) {
                    return cb.between(expression, timeRange[0], timeRange[1]);
                } else {
                    return null;
                }

                //
            case notNull:
                if (Boolean.valueOf(value.toString())) {
                    return cb.isNotNull(expression);
                }
        }
        return null;
    }
}
