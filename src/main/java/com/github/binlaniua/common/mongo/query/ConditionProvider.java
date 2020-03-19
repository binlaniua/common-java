package com.github.binlaniua.common.mongo.query;


import org.springframework.data.mongodb.core.query.Query;

public interface ConditionProvider {

    void provider(Query query);
}
