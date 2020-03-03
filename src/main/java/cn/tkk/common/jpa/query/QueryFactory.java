package cn.tkk.common.jpa.query;

import cn.tkk.common.jpa.query.config.*;
import cn.tkk.common.jpa.query.value.FetchItem;
import cn.tkk.common.jpa.query.value.QueryItem;
import cn.tkk.common.jpa.query.value.SortItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;


/**
 * Created by Tkk on 2018/7/21.
 */
@Slf4j
public class QueryFactory {

    @Data
    @AllArgsConstructor
    private static class QueryDesc {
        Field field;
        Query[] queryItem;
        boolean isAnd;
    }

    /**
     * 查询缓存
     */
    private static final Map<Class, List<QueryDesc>> classQueryDescMap = new HashMap<>();


    /**
     * @param object
     * @param
     * @return
     */
    public static <T> Specification<T> build(final Object object) {
        final Class clazz = object.getClass();
        final PredicateBuilder<T> predicateBuilder = new PredicateBuilder<T>();

        // 设置级联抓取
        final JoinFetchs joinFetchs = (JoinFetchs) clazz.getAnnotation(JoinFetchs.class);
        if (joinFetchs != null) {
            for (final JoinFetch joinFetch : joinFetchs.value()) {
                predicateBuilder.fetch(new FetchItem(joinFetch.column(), joinFetch.join(), joinFetch.queryJoin()));
            }
        }

        // 设置排序
        final Sorts sorts = (Sorts) clazz.getAnnotation(Sorts.class);
        if (sorts != null) {
            for (final Sort sort : sorts.value()) {
                predicateBuilder.sort(new SortItem(sort.column(), sort.desc()));
            }
        }

        // 自定义查询
        if (object instanceof PredicateProvider) {
            ((PredicateProvider) object).provider(predicateBuilder);
        }

        // 构造查询条件
        final List<QueryDesc> queryDescMap = getQueryDescMap(clazz);
        for (final QueryDesc desc : queryDescMap) {

            // 获取查询值
            final String fieldName = desc.field.getName();
            final Object value = getValue(desc.field, object);

            //
            final QueryItem[] queryItems = new QueryItem[desc.queryItem.length];
            for (int i = 0; i < desc.queryItem.length; i++) {
                final Query query = desc.queryItem[i];
                final String column = StringUtils.isBlank(query.column()) ? fieldName : query.column();
                queryItems[i] = new QueryItem(
                        column,
                        value,
                        query.type(),
                        query.join(),
                        query.nullable(),
                        query.blankable()
                );
            }

            //
            if (desc.isAnd) {
                predicateBuilder.and(queryItems);
            } else {
                predicateBuilder.or(queryItems);
            }
        }


        /**
         *
         */
        return predicateBuilder.to();
    }

    /**
     * @param field
     * @param src
     * @return
     */
    private static Object getValue(final Field field, final Object src) {
        try {
            return field.get(src);
        } catch (final IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 抽取出这个对象的查询条件
     *
     * @param clazz
     * @return
     */
    private static List<QueryDesc> getQueryDescMap(final Class clazz) {
        return classQueryDescMap.computeIfAbsent(clazz, c -> {
            final List<QueryDesc> real = new ArrayList<>();
            ReflectionUtils.doWithFields(clazz, (field) -> {
                Optional.ofNullable(getQueryDec(field))
                        .ifPresent(qd -> {
                            field.setAccessible(true);
                            real.add(qd);
                        });
            });
            return real;
        });
    }

    /**
     * 获取该字段查询
     *
     * @param field
     * @return
     */
    private static QueryDesc getQueryDec(final Field field) {
        // 是否有一个字段查询多个条件
        final Querys queryItems = field.getAnnotation(Querys.class);
        if (queryItems != null) {
            return new QueryDesc(field, queryItems.value(), queryItems.isAnd());
        }

        // 这是查询一个
        final Query annotation = field.getAnnotation(Query.class);
        if (annotation == null) {
            return null;
        }
        return new QueryDesc(field, new Query[]{annotation}, true);
    }
}
