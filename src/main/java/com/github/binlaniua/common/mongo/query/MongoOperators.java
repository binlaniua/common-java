package com.github.binlaniua.common.mongo.query;


import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MongoOperators {

    MongoOperator[] value();

    boolean isAnd() default false;
}
