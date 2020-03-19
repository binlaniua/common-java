package com.github.binlaniua.common.jpa.query.factory;

import cn.tkk.common.jpa.query.config.QueryJoinType;
import cn.tkk.common.jpa.query.value.FetchItem;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.hibernate.query.criteria.internal.path.ListAttributeJoin;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class PredicateFetchFactory {

    /**
     * join
     */
    private final Map<String, Join> joinMap = new HashMap<>();

    /**
     * 是否需要fetch, 比如 select count 这些就是不要的
     */
    @Getter
    private boolean fetch;

    /**
     * a.b.c.d
     *
     * @param root
     * @param fetchList
     */
    public static PredicateFetchFactory build(final Root root, final CriteriaQuery criteriaQuery, final List<FetchItem> fetchList) {
        final PredicateFetchFactory predicateFetchFactory = new PredicateFetchFactory();

        //
        predicateFetchFactory.fetch = needFetch(criteriaQuery);

        //
        if (predicateFetchFactory.fetch) {
            for (final FetchItem fetch : fetchList) {
                final String[] split = StringUtils.split(fetch.getColumn(), ".");
                Join parentJoin = (Join) root;
                for (int i = 0; i < split.length; i++) {
                    //
                    final String fetchEntity = split[i];
                    parentJoin = (Join) parentJoin.fetch(fetchEntity, fetch.getJoin());
                    predicateFetchFactory.joinMap.put(fetchEntity, parentJoin);

                    // 如果是onetomany类型fetch, 必须做distinct
                    if (parentJoin instanceof ListAttributeJoin) {
                        criteriaQuery.distinct(true);
                    }
                }
            }
        }
        return predicateFetchFactory;
    }

    /**
     * 是否能用fetch关联获取
     *
     * @param criteriaQuery
     * @return
     */
    public static boolean needFetch(final CriteriaQuery<?> criteriaQuery) {
        final Class<?> resultType = criteriaQuery.getResultType();
        if (resultType.isAssignableFrom(Long.class)) {
            return false;
        }
        if (resultType.isAssignableFrom(Integer.class)) {
            return false;
        }
        if (resultType.isAssignableFrom(Boolean.class)) {
            return false;
        }
        if (resultType.isAssignableFrom(Double.class)) {
            return false;
        }
        return true;
    }

    /**
     * @param parentJoin
     * @param joinColumn
     * @param queryJoinType
     * @return
     */
    public Join getJoin(final Join parentJoin, final String joinColumn, final QueryJoinType queryJoinType) {
        if (this.fetch && this.joinMap.containsKey(joinColumn)) {
            return this.joinMap.get(joinColumn);
        }
        return parentJoin.join(joinColumn, queryJoinType.toJoinType());
    }
}
