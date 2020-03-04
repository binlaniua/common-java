package cn.tkk.common.jpa.query;

import cn.tkk.common.jpa.query.config.QueryJoinType;
import cn.tkk.common.jpa.query.value.ExistColumn;
import cn.tkk.common.jpa.query.value.FetchItem;
import cn.tkk.common.jpa.query.value.QueryItem;
import cn.tkk.common.jpa.query.value.SortItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Kun Tang on 2018/10/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredicateBuilder<T> implements Specification<T> {


    @Data
    @AllArgsConstructor
    private static class QueryItems {
        private List<QueryItem> items;
        private boolean and;
    }

    /**
     * @return
     */
    public static PredicateBuilder builder() {
        return new PredicateBuilder();
    }

    private List<QueryItems> whereList = new LinkedList<>();
    private List<ExistColumn> whereExistList = new LinkedList<>();
    private List<FetchItem> fetchList = new LinkedList<>();
    private List<SortItem> sortList = new LinkedList<>();
    private Map<String, Join> joinMap = new HashMap<>();
    private boolean isFetchJoin;

    private Object values;


    /**
     * 抓取
     *
     * @param fetchItem
     * @return
     */
    public PredicateBuilder fetch(final FetchItem fetchItem) {
        this.fetchList.add(fetchItem);
        return this;
    }

    /**
     * 排序
     *
     * @param sortItem
     * @return
     */
    public PredicateBuilder sort(final SortItem sortItem) {
        this.sortList.add(sortItem);
        return this;
    }

    /**
     * and
     *
     * @param queryItems
     * @return
     */
    public PredicateBuilder and(final QueryItem... queryItems) {
        final List<QueryItem> items = Stream.of(queryItems)
                                            .filter(v -> v.valid())
                                            .collect(Collectors.toList());
        if (!items.isEmpty()) {
            this.whereList.add(new QueryItems(items, true));
        }
        return this;
    }

    /**
     * exists
     *
     * @param existColumn
     * @return
     */
    public PredicateBuilder exist(final ExistColumn existColumn) {
        if (existColumn == null) {
            return this;
        }
        this.whereExistList.add(existColumn);
        return this;
    }

    /**
     * or
     *
     * @param queryItems
     * @return
     */
    public PredicateBuilder or(final QueryItem... queryItems) {
        final List<QueryItem> items = Stream.of(queryItems)
                                            .filter(v -> v.valid())
                                            .collect(Collectors.toList());
        if (!items.isEmpty()) {
            this.whereList.add(new QueryItems(items, false));
        }
        return this;
    }

    /**
     * @param root
     * @param criteriaQuery
     * @param criteriaBuilder
     * @return
     */
    public List<Predicate> toPredicateCondition(final Root<T> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = new LinkedList<>();

        // 根据返回类型判断是否需要进行级联
        // 比如 select count(x) 这种就不要级联, 只是需要判断
        this.isFetchJoin = this.needFetch(criteriaQuery);

        // 关联抓取
        if (!this.fetchList.isEmpty() && this.isFetchJoin) {
            for (final FetchItem fetch : this.fetchList) {
                final String[] split = StringUtils.split(fetch.getColumn(), ".");
                Join base = null;
                for (int i = 0; i < split.length; i++) {
                    final String fetchEntity = split[i];
                    base = (Join) (i == 0 ? root : base).fetch(fetchEntity, fetch.getJoin());
                    if (fetch.isQueryJoin()) {
                        this.joinMap.put(fetchEntity, base);
                    }
                }
            }
        }

        // 条件
        for (final QueryItems where : this.whereList) {
            final Predicate[] r = this.doWhere(where.getItems(), root, criteriaQuery, criteriaBuilder);
            if (r.length == 0) {
                continue;
            }
            if (where.and) {
                predicates.add(criteriaBuilder.and(r));
            } else {
                predicates.add(criteriaBuilder.or(r));
            }
        }

        //
        for (final ExistColumn existColumn : this.whereExistList) {
            final Predicate predicate = this.toPredicateExist(existColumn, root, criteriaQuery, criteriaBuilder);
            if (predicate != null) {
                predicates.add(criteriaBuilder.and(predicate));
            }
        }


        // 排序
        if (!this.sortList.isEmpty() && this.isFetchJoin) {
            for (final SortItem sort : this.sortList) {
                final Expression expression = this.toExpression(root, sort.getColumn(), null);
                if (sort.isDesc()) {
                    criteriaQuery.orderBy(criteriaBuilder.desc(expression));
                } else {
                    criteriaQuery.orderBy(criteriaBuilder.asc(expression));
                }
            }
        }

        //
        return predicates;
    }


    /**
     * @param root
     * @param criteriaQuery
     * @param criteriaBuilder
     * @return
     */
    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        final List<Predicate> predicates = this.toPredicateCondition(root, criteriaQuery, criteriaBuilder);
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    /**
     * @param items
     * @param root
     * @param criteriaQuery
     * @param criteriaBuilder
     * @return
     */
    private Predicate[] doWhere(final List<QueryItem> items, final Root<T> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        return items
                .stream()
                .map((item) -> this.doQueryItem(item, root, criteriaQuery, criteriaBuilder))
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);
    }


    /**
     * @param existColumn
     * @param root
     * @param criteriaQuery
     * @param criteriaBuilder
     * @return
     */
    private Predicate toPredicateExist(final ExistColumn existColumn, final Root<T> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        if (existColumn.getColumnValueList()
                       .isEmpty()) {
            return null;
        }
        //
        final Subquery subquery = criteriaQuery.subquery(existColumn.getColumnClass());
        final Root from = subquery.from(existColumn.getThirdTableEntityClass());

        //
        final CriteriaBuilder.In id = criteriaBuilder.in(this.toExpression(from, existColumn.getColumn(), QueryJoinType.Left));
        existColumn.getColumnValueList()
                   .forEach(tag -> id.value(tag));
        subquery.select(criteriaBuilder.literal(1L));
        subquery.where(criteriaBuilder.and(
                id,
                criteriaBuilder.equal(root.get("id"), from.get(existColumn.getExistColumn()))
        ));
        return criteriaBuilder.exists(subquery);
    }

    private Expression toExpression(final Root<T> root, final String column, final QueryJoinType queryJoinType) {
        final String[] split = StringUtils.split(column, ".");
        if (split.length == 1) {
            return root.get(split[0]);
        }
        Join join = null;
        for (int i = 0; i < split.length - 1; i++) {
            final String joinEntity = split[i];
            // 已经被fetch过了
            if (this.isFetchJoin && this.joinMap.containsKey(joinEntity)) {
                join = this.joinMap.get(joinEntity);
                continue;
            }
            //
            final JoinType joinType;
            switch (queryJoinType) {
                case Left:
                    joinType = JoinType.LEFT;
                    break;
                case Right:
                    joinType = JoinType.RIGHT;
                    break;
                default:
                    joinType = JoinType.INNER;
                    break;
            }
            if (i == 0) {
                join = root.join(split[i], joinType);
            } else {
                join = join.join(split[i], joinType);
            }
        }
        return join.get(split[split.length - 1]);
    }

    private Predicate doQueryItem(final QueryItem item, final Root<T> root, final CriteriaQuery<?> criteriaQuery, final CriteriaBuilder criteriaBuilder) {
        return QueryCondition.toCondition(
                item,
                criteriaBuilder,
                criteriaQuery,
                this.toExpression(root, item.getColumn(), item.getJoin())
        );
    }

    /**
     * 是否能用fetch关联获取
     *
     * @param criteriaQuery
     * @return
     */
    public boolean needFetch(final CriteriaQuery<?> criteriaQuery) {
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


    public Specification to() {
        return this::toPredicate;
    }
}
