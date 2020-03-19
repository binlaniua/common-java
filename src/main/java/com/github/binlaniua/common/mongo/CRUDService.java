package com.github.binlaniua.common.mongo;


import cn.tkk.common.exception.SystemException;
import cn.tkk.common.mongo.query.Condition;
import cn.tkk.common.request.AddRequest;
import cn.tkk.common.request.EditRequest;
import cn.tkk.common.request.InfoRequest;
import cn.tkk.common.request.PageRequest;
import cn.tkk.common.util.BeanHelper;
import com.github.binlaniua.common.exception.SystemException;
import com.github.binlaniua.common.request.AddRequest;
import com.github.binlaniua.common.request.EditRequest;
import com.github.binlaniua.common.request.InfoRequest;
import com.github.binlaniua.common.request.PageRequest;
import com.github.binlaniua.common.util.BeanHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 动态分页
 *
 * @param <Entity>
 */
@Slf4j
@Transactional(readOnly = true)
public abstract class CRUDService<Entity extends Domain<ID>, ID extends Serializable> implements ApplicationContextAware, InitializingBean {

    protected Class<Entity> entityClass = (Class<Entity>) ((ParameterizedType) this.getClass()
                                                                                   .getGenericSuperclass()).getActualTypeArguments()[0];

    @Autowired
    private MongoRepository<Entity, ID> mongoRepository;

    @Autowired
    private MongoDao<Entity, ID> mongoDao;

    /**
     * 分页
     *
     * @param request
     * @return
     */
    public <T> Page<T> page(final PageRequest request, final Function<Entity, T> fun) {
        return this.mongoDao.queryPage(request, fun);
    }

    /**
     * 分页
     *
     * @param request
     * @return
     */
    public Page<Entity> page(final PageRequest request) {
        return this.mongoDao.queryPage(request);
    }

    /**
     * 所有
     *
     * @param request
     * @return
     */
    public <T> List<T> all(final PageRequest request, final Function<Entity, T> fun) {
        return this.mongoDao.list(request, fun);
    }

    /**
     * 所有
     *
     * @param request
     * @return
     */
    public List<Entity> all(final Object request) {
        return this.mongoDao.list(request);
    }


    /**
     * 所有
     *
     * @param request
     * @return
     */
    public List<Entity> all(final Object request, final int page, final int size) {
        return this.mongoDao.list(request, page, size);
    }

    /**
     * @param idList
     * @return
     */
    public List<Entity> all(final List<ID> idList) {
        return Lists.newArrayList(this.mongoRepository.findAllById(idList));
    }

    /**
     * @param
     * @return
     */
    public List<Entity> all() {
        return this.mongoRepository.findAll();
    }

    /**
     * @param <KEY>
     * @param group
     * @return
     */
    public <KEY> Map<KEY, List<Entity>> map(final Function<Entity, KEY> group) {
        return this.map(null, group);
    }

    /**
     * @param <KEY>
     * @return
     */
    public <KEY> Map<KEY, Entity> mapDistinct(final Function<Entity, KEY> distinct) {
        return this.mapDistinct(null, distinct);
    }


    /**
     * @param <KEY>
     * @param idList
     * @param group
     * @return
     */
    public <KEY> Map<KEY, List<Entity>> map(final List<ID> idList, final Function<Entity, KEY> group) {
        final List<Entity> entityList = CollectionUtils.isEmpty(idList) ? this.all() : this.all(idList);
        return entityList.stream()
                         .collect(Collectors.groupingBy(group));
    }

    /**
     * @param <KEY>
     * @param idList
     * @return
     */
    public <KEY> Map<KEY, Entity> mapDistinct(final List<ID> idList, final Function<Entity, KEY> distinct) {
        final List<Entity> entityList = CollectionUtils.isEmpty(idList) ? this.all() : this.all(idList);
        return entityList
                .stream()
                .collect(Collectors.toMap(distinct, Function.identity()));
    }


    /**
     * 删除
     *
     * @param idList
     * @return
     */
    @Transactional()
    public void remove(final List<ID> idList) {
        this.remove(idList, null);
    }

    /**
     * @param idList
     * @param consumer
     */
    @Transactional
    public void remove(final List<ID> idList, final Consumer<ID> consumer) {
        idList.forEach(id -> {
            if (consumer != null) {
                consumer.accept(id);
            }
            this.mongoRepository.deleteById(id);
        });
    }

    /**
     * 查询
     *
     * @param id
     * @return
     */
    public Entity find(final ID id) {
        if (id == null) {
            return null;
        }
        final Optional<Entity> one = this.mongoRepository.findById(id);
        if (!one.isPresent()) {
            throw new SystemException("000009");
        }
        return one.get();
    }

    /**
     * 查询
     *
     * @param id
     * @param function
     * @return
     */
    public <T> T find(final ID id, final Function<Entity, T> function) {
        return function.apply(this.find(id));
    }

    /**
     * 查询
     *
     * @param id
     * @return
     */
    public Entity findOrCreate(final ID id) {
        return this.findOrCreate(id, () -> {
            try {
                return this.entityClass.newInstance();
            } catch (final Exception e) {
                throw new SystemException(e.getMessage(), e);
            }
        });
    }


    /**
     * 查询
     *
     * @param id
     * @return
     */
    public Entity findOrCreate(final ID id, final Supplier<Entity> supplier) {
        try {
            if (id == null) {
                return supplier.get();
            }
            return this.mongoRepository.findById(id)
                                       .orElse(supplier.get());
        } catch (final Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /**
     * @param id
     * @param consumer
     */
    public void findAndExec(final ID id, final Consumer<Entity> consumer) {
        final Entity entity = this.find(id);
        consumer.accept(entity);
    }

    /**
     * @param id
     * @param consumer
     */
    public void findAndSave(final ID id, final Consumer<Entity> consumer) {
        final Entity entity = this.find(id);
        consumer.accept(entity);
        this.save(entity);
    }


    /**
     * 查询
     *
     * @param request
     * @return
     */
    public Entity find(final InfoRequest request) {
        final Entity one = this.mongoDao.one(Condition.build(request));
        if (one == null) {
            throw new SystemException("000009");
        }
        return one;
    }

    /**
     * 查询
     *
     * @param request
     * @param function
     * @return
     */
    public <T> T find(final InfoRequest request, final Function<Entity, T> function) {
        final Entity entity = this.find(request);
        return function.apply(entity);
    }

    /**
     * 编辑
     *
     * @param entity
     * @return
     */
    @Transactional
    public Entity edit(final Entity entity) {
        return this.edit(entity, null);
    }

    /**
     * 编辑
     *
     * @param entity
     * @param consumer
     * @return
     */
    @Transactional
    public Entity edit(final Entity entity, final Consumer<Entity> consumer) {
        final Entity newEntity = this.findOrCreate(entity.getId());
        BeanHelper.copyProperties(newEntity, entity);
        if (consumer != null) {
            consumer.accept(newEntity);
        }
        return this.save(newEntity);
    }


    /**
     * 编辑
     *
     * @param request
     * @return
     */
    @Transactional
    public Entity edit(final EditRequest<ID> request) {
        return this.edit(request, null);
    }

    /**
     * 编辑
     *
     * @param request
     * @return
     */
    @Transactional
    public Entity edit(final EditRequest<ID> request, final Consumer<Entity> consumer) {
        final Entity entity = this.findOrCreate(request.getId());
        BeanHelper.copyProperties(entity, request);
        if (consumer != null) {
            consumer.accept(entity);
        }
        return this.save(entity);
    }

    /**
     * @param request
     * @return
     */
    @Transactional
    public Entity add(final AddRequest request) {
        return this.add(request, null);
    }

    /**
     * @param request
     * @return
     */
    @Transactional
    public Entity add(final AddRequest request, final Consumer<Entity> consumer) {
        try {
            final Entity entity = this.entityClass.newInstance();
            BeanHelper.copyProperties(entity, request);
            if (consumer != null) {
                consumer.accept(entity);
            }
            return this.save(entity);
        } catch (final Exception e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /**
     * 新增
     *
     * @param m
     */
    @Transactional
    public Entity save(final Entity m) {
        return this.mongoRepository.save(m);
    }

    /**
     * @param id
     * @return
     */
    public boolean exist(final ID id) {
        return this.mongoRepository.existsById(id);
    }

    /**
     * @param query
     * @return
     */
    public boolean exist(final Object query) {
        return this.mongoDao.count(Condition.build(query)) > 0;
    }

    /**
     * @param query
     * @return
     */
    public Long count(final Object query) {
        return this.mongoDao.count(Condition.build(query));
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }


}
