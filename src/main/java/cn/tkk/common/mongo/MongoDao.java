package cn.tkk.common.mongo;

import cn.tkk.common.mongo.query.Condition;
import cn.tkk.common.request.PageRequest;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MongoDao<T extends Domain<ID>, ID extends Serializable> {

    protected Class<T> entityClass = (Class<T>) ((ParameterizedType) this.getClass()
                                                                         .getGenericSuperclass()).getActualTypeArguments()[0];

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * @param query
     * @param update
     * @return
     */
    public int upsert(final Query query, final Update update) {
        final UpdateResult result = this.mongoTemplate.upsert(query, update, this.entityClass);
        return (int) result.getModifiedCount();
    }

    /**
     * @param list
     */
    public void insert(final Collection<? extends Object> list) {
        this.mongoTemplate.insert(list, this.entityClass);
    }

    /**
     * @param query
     * @return
     */
    public boolean exists(final Query query) {
        return this.mongoTemplate.exists(query, this.entityClass);
    }

    /**
     * @param id
     * @param update
     * @return
     */
    public int update(final String id, final Update update) {
        return this.update(new Query(Criteria.where("id")
                                             .is(id)), update, true);
    }

    /**
     * @param query
     * @param update
     * @return
     */
    public int update(final Query query, final Update update) {
        return this.update(query, update, true);
    }

    /**
     * @param query
     * @param update
     * @param all
     * @return
     */
    public int update(final Query query, final Update update, final boolean all) {
        final UpdateResult result = all ? this.mongoTemplate.updateMulti(query, update, this.entityClass) : this.mongoTemplate.updateFirst(query, update, this.entityClass);
        return (int) result.getModifiedCount();
    }

    /**
     * @param query
     * @return
     */
    public List<T> list(final Query query) {
        return this.mongoTemplate.find(query, this.entityClass);
    }

    /**
     * @param o
     * @return
     */
    public List<T> list(final Object o) {
        final Query build = Condition.build(o);
        return this.mongoTemplate.find(build, this.entityClass);
    }

    /**
     * @param o
     * @param function
     * @param <R>
     * @return
     */
    public <R> List<R> list(final Object o, final Function<T, R> function) {
        final Query build = Condition.build(o);
        return this.list(build, function);
    }

    /**
     * @param query
     * @param function
     * @param <R>
     * @return
     */
    public <R> List<R> list(final Query query, final Function<T, R> function) {
        return this.mongoTemplate.find(query, this.entityClass)
                                 .stream()
                                 .map(function)
                                 .collect(Collectors.toList());
    }

    /**
     * @param request
     * @param page
     * @param size
     * @return
     */
    public List<T> list(final Object request, final int page, final int size) {
        final Query build = Condition.build(request);
        build.skip(page * size);
        build.limit(size);
        return this.mongoTemplate.find(build, this.entityClass);
    }

    /**
     * @param query
     * @return
     */
    public T one(final Query query) {
        return this.mongoTemplate.findOne(query, this.entityClass);
    }

    /**
     * @param query
     * @return
     */
    public Long count(final Query query) {
        return this.mongoTemplate.count(query, this.entityClass);
    }

    /**
     * @param query
     */
    public void delete(final Query query) {
        this.mongoTemplate.remove(query, this.entityClass);
    }

    /**
     * @param object
     * @return
     */
    public Page<T> queryPage(final PageRequest object) {
        return this.queryPage(object, Function.identity());
    }

    /**
     * @param object
     * @param converter
     * @param <S>
     * @return
     */
    public <S> Page<S> queryPage(final PageRequest object, final Function<T, S> converter) {
        final Query query = Condition.build(object);
        final long totalCount = this.count(query);
        final List<S> dataList = this.list(query, converter);
        return new PageImpl<>(dataList, object.getPage(), totalCount);
    }


}
