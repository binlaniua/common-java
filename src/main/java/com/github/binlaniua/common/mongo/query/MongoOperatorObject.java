package com.github.binlaniua.common.mongo.query;


import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MongoOperatorObject {

    String name() default "";

}
