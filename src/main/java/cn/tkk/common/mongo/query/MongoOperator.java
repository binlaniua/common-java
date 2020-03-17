package cn.tkk.common.mongo.query;


import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MongoOperator {

    Type value() default Type.IS;

    String name() default "";

    String belongName() default "";

    enum Type {
        IS,
        LIKE,
        EXISTS,
        GT,
        GTE,
        LT,
        LTE,
        NOT,
        GEO,
        RANGE, DISTANCE
    }
}
