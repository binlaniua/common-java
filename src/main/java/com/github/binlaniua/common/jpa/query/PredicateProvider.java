package com.github.binlaniua.common.jpa.query;

/**
 * 额外查询提供
 */
public interface PredicateProvider {

    /**
     *
     * @param predicateBuilder
     */
    void provider(PredicateBuilder predicateBuilder);
}
