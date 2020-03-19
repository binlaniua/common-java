package com.github.binlaniua.common.jpa.query;

import cn.tkk.common.jpa.query.config.QueryJoinType;
import cn.tkk.common.jpa.query.factory.PredicateFetchFactory;
import cn.tkk.common.jpa.query.value.ExistColumn;
import cn.tkk.common.jpa.query.value.FetchItem;
import cn.tkk.common.jpa.query.value.QueryItem;
import cn.tkk.common.jpa.query.value.SortItem;
import com.github.binlaniua.common.jpa.query.config.QueryJoinType;
import com.github.binlaniua.common.jpa.query.factory.PredicateFetchFactory;
import com.github.binlaniua.common.jpa.query.value.ExistColumn;
import com.github.binlaniua.common.jpa.query.value.FetchItem;
import com.github.binlaniua.common.jpa.query.value.QueryItem;
import com.github.binlaniua.common.jpa.query.value.SortItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

    /**
     *
     */
    private PredicateFetchFactory fetchFactory;
    private List<QueryItems> whereList = new LinkedList<>();
    private List<ExistColumn> whereExistList = new LinkedList<>();
    private List<FetchItem> fetchList = new LinkedList<>();
    private List<SortItem> sortList = new LinkedList<>();

    /**
     *
     */
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
     * 抓取
     *
     * @param column
     * @param joinType
     * @return
     */
    public PredicateBuilder fetch(final String column, final JoinType joinType) {
        return this.fetch(new FetchItem(column, joinType));
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
     * 排序
     *
     * @param column
     * @param isDesc
     * @return
     */
    public PredicateBuilder sort(final String column, final boolean isDesc) {
        return this.sort(new SortItem(column, isDesc));
    }

    /**
     * 清空排序
     *
     * @return
     */
    public PredicateBuilder sortClean() {
        this.sortList.clear();
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
                                            .filter(QueryItem::valid)
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
        if (existColumn != null) {
            this.whereExistList.add(existColumn);
        }
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
                                            .filter(QueryItem::valid)
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

        //
        this.fetchFactory = PredicateFetchFactory.build(root, criteriaQuery, this.fetchList);

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

        // exist 查询
        for (final ExistColumn existColumn : this.whereExistList) {
            final Predicate predicate = this.toPredicateExist(existColumn, root, criteriaQuery, criteriaBuilder);
            if (predicate != null) {
                predicates.add(criteriaBuilder.and(predicate));
            }
        }


        // 排序
        if (this.fetchFactory.isFetch()) {
            for (final SortItem sort : this.sortList) {
                final Expression expression = this.toExpression(root, sort.getColumn(), QueryJoinType.Inner);
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
            join = this.fetchFactory.getJoin(i == 0 ? (Join) root : join, split[i], queryJoinType);
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


    public Specification to() {
        return this::toPredicate;
    }
}
