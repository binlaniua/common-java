package cn.tkk.common.mongo.query;

import cn.tkk.common.jpa.query.value.SortItem;
import cn.tkk.common.request.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Condition {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class FieldCondition {
        Field field;
        MongoOperator[] operators;
        MongoOperatorObject nestObject;
        boolean and;
        boolean multi;
        List<FieldCondition> fieldConditionList;
    }

    private static final Map<Class, List<FieldCondition>> cache = new ConcurrentHashMap<>();

    public static Query build(final Object o) {
        final Condition condition = new Condition();
        condition.addQuery(o);
        if (o instanceof ConditionProvider) {
            final ConditionProvider cp = (ConditionProvider) o;
            cp.provider(condition.query);
        }
        if (o instanceof PageRequest) {
            final PageRequest pp = (PageRequest) o;
            condition.setOffset(pp.getPageNum(), pp.getSize());
            Optional.ofNullable(pp.getSorts())
                    .ifPresent((vs) -> {
                        for (final SortItem s : vs) {
                            condition.query.with(new Sort(s.isDesc() ? Sort.Direction.DESC : Sort.Direction.ASC, s.getColumn()));
                        }
                    });
        }
        return condition.getQuery();
    }

    @Getter
    private final Query query = new Query();

    public void addQuery(final Object object) {
        this.addQuery(object, null);
    }

    public void addQuery(final Object object, final FieldCondition nestObject) {
        final List<FieldCondition> fieldConditions = toFieldConditions(object.getClass());
        for (final FieldCondition fieldCondition : fieldConditions) {
            final Object value = getValue(fieldCondition.getField(), object);
            if (value == null) {
                continue;
            }

            // 适配与子查询
            if (fieldCondition.getFieldConditionList() != null) {
                this.addQuery(value, fieldCondition);
                continue;
            }

            //
            final Criteria[] criteriaList = criteriaList(value, fieldCondition, nestObject);

            //
            switch (criteriaList.length) {
                case 0:
                    break;
                case 1:
                    this.query.addCriteria(criteriaList[0]);
                    break;
                default:
                    final Criteria link = new Criteria();
                    if (fieldCondition.isAnd()) {
                        link.andOperator(criteriaList);
                    } else {
                        link.orOperator(criteriaList);
                    }
                    this.query.addCriteria(link);
            }
        }
    }

    public void setOffset(final int page, final int size) {
        this.query.skip(page * size);
        this.query.limit(size);
    }

    public void limit(final int limit) {
        this.query.limit(limit);
    }

    private static Criteria[] criteriaList(final Object value, final FieldCondition fieldCondition, final FieldCondition nestObject) {
        final List<Criteria> criteria = new ArrayList<>();
        String nestName = StringUtils.EMPTY;
        if (nestObject != null) {
            if (StringUtils.isBlank(nestObject.nestObject.name())) {
                nestName = nestObject.field.getName() + ".";
            } else {
                nestName = nestObject.nestObject.name() + ".";
            }
        }

        for (final MongoOperator operator : fieldCondition.operators) {
            String name = operator.name();
            if (StringUtils.isBlank(name)) {
                name = fieldCondition.getField()
                                     .getName();
            }
            final Criteria where = Criteria.where(nestName + name);
            if (appendCriteria(where, operator.value(), value)) {
                criteria.add(where);
            }
        }
        return criteria.toArray(new Criteria[criteria.size()]);
    }

    private static Object getValue(final Field field, final Object src) {
        try {
            return field.get(src);
        } catch (final IllegalAccessException e) {
            return null;
        }
    }


    private static List<FieldCondition> toFieldConditions(final Class clazz) {
        List<FieldCondition> result = cache.get(clazz);
        if (result != null) {
            return result;
        }
        synchronized (clazz.getName()) {
            result = cache.get(clazz);
            if (result != null) {
                return result;
            }

            // 组装
            final List<FieldCondition> real = new ArrayList<>();
            ReflectionUtils.doWithFields(clazz, (field) -> {
                final FieldCondition queryDec = toFieldCondition(field);
                if (queryDec == null) {
                    return;
                }
                field.setAccessible(true);
                real.add(queryDec);
            });

            //
            return real;
        }
    }

    /**
     * @param field
     * @return
     */
    private static FieldCondition toFieldCondition(final Field field) {
        //
        final MongoOperators queryItems = field.getAnnotation(MongoOperators.class);
        final MongoOperator queryItem = field.getAnnotation(MongoOperator.class);
        final MongoOperatorObject queryObject = field.getAnnotation(MongoOperatorObject.class);

        //
        final FieldCondition fieldCondition = new FieldCondition();
        if (queryItems != null) { //一个字段匹配多个查询条件
            fieldCondition.setOperators(queryItems.value());
            fieldCondition.setAnd(queryItems.isAnd());
            fieldCondition.setMulti(true);
        } else if (queryItem != null) { //一个字段匹配一个
            fieldCondition.setOperators(new MongoOperator[]{queryItem});
        } else if (queryObject != null) { //一个字段的属性会有多个查询条件
            fieldCondition.setFieldConditionList(toFieldConditions(field.getType()));
            fieldCondition.setNestObject(queryObject);
        } else {
            return null;
        }

        //
        fieldCondition.setField(field);
        return fieldCondition;
    }


    public static boolean appendCriteria(final Criteria criteria, final MongoOperator.Type type, final Object value) {

        switch (type) {
            //
            case IS:
                if (value instanceof List) {
                    if (!((List) value).isEmpty()) {
                        criteria.in((List) value);
                    } else {
                        return false;
                    }
                } else if (value instanceof String) {
                    if (StringUtils.isNotBlank((CharSequence) value)) {
                        criteria.is(value);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    criteria.is(value);
                }
                return true;
            //
            case LIKE:
                criteria.regex("^.*" + value.toString() + ".*$", "i"); //忽略大小写
                return true;
            //
            case EXISTS:
                return true;

            //
            case LT:
                criteria.lt(value);
                return true;
            //
            case GT:
                criteria.gt(value);
                return true;
            //
            case LTE:
                criteria.lte(value);
                return true;
            //
            case GTE:
                criteria.gte(value);
                return true;
            //
            case RANGE:
                final Object[] rangeObjs = (Object[]) value;
                if (rangeObjs.length != 2) {
                    return false;
                }
                if (Objects.nonNull(rangeObjs[0]) && Objects.nonNull(rangeObjs[1])) {
                    criteria.gte(rangeObjs[0])
                            .lte(rangeObjs[1]);
                    return true;
                } else if (Objects.nonNull(rangeObjs[0])) {
                    criteria.gte(rangeObjs[0]);
                    return true;
                } else if (Objects.nonNull(rangeObjs[1])) {
                    criteria.lte(rangeObjs[1]);
                    return true;
                }
                return false;

        }

        return false;
    }
}
