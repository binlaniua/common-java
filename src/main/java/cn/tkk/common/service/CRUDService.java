package cn.tkk.common.service;


import cn.tkk.common.exception.SystemException;
import cn.tkk.common.request.AddRequest;
import cn.tkk.common.request.EditRequest;
import cn.tkk.common.request.InfoRequest;
import cn.tkk.common.request.PageRequest;
import cn.tkk.common.util.BeanHelper;
import cn.tkk.common.jpa.Domain;
import cn.tkk.common.jpa.PageRepository;
import cn.tkk.common.jpa.query.PredicateBuilder;
import cn.tkk.common.jpa.query.QueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
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
@Service
@Transactional(readOnly = true)
public abstract class CRUDService<Entity extends Domain<ID>, ID extends Serializable> implements ApplicationContextAware, InitializingBean {

    protected Class<Entity> entityClass = (Class<Entity>) ((ParameterizedType) this.getClass()
                                                                                   .getGenericSuperclass()).getActualTypeArguments()[0];

    @Autowired
    private JpaSpecificationExecutor<Entity> jpaSpecificationExecutor;

    @Autowired
    private JpaRepository<Entity, ID> jpaRepository;

    @Autowired
    private PageRepository pageRepository;

    /**
     * 分页
     *
     * @param request
     * @return
     */
    public <T> Page<T> page(final PageRequest request, final Function<Entity, T> fun) {
        return this.jpaSpecificationExecutor.findAll(QueryFactory.build(request), request.getPage())
                                            .map(fun);
    }

    /**
     * 分页
     *
     * @param request
     * @return
     */
    public Page<Entity> page(final PageRequest request) {
        return this.jpaSpecificationExecutor.findAll(QueryFactory.build(request), request.getPage());
    }

    /**
     * 所有
     *
     * @param request
     * @return
     */
    public <T> List<T> all(final PageRequest request, final Function<Entity, T> fun) {
        final List<T> all = this.jpaSpecificationExecutor.findAll(QueryFactory.build(request))
                                                         .stream()
                                                         .map(fun)
                                                         .collect(Collectors.toList());
        return all;
    }

    /**
     * 所有
     *
     * @param request
     * @return
     */
    public List<Entity> all(final PageRequest request) {
        final List<Entity> all = this.jpaSpecificationExecutor.findAll(QueryFactory.build(request));
        return all;
    }

    /**
     * 所有
     *
     * @param request
     * @return
     */
    public List<Entity> all(final PredicateBuilder<Entity> request) {
        final List<Entity> all = this.jpaSpecificationExecutor.findAll(request);
        return all;
    }

    /**
     * 所有
     *
     * @param request
     * @return
     */
    public List<Entity> all(final PredicateBuilder<Entity> request, final int page, final int size) {
        final Page<Entity> all = this.jpaSpecificationExecutor.findAll(request, org.springframework.data.domain.PageRequest.of(page, size));
        return all.getContent();
    }

    /**
     * @param idList
     * @return
     */
    public List<Entity> all(final List<ID> idList) {
        return idList.isEmpty() ? Collections.emptyList() : this.jpaRepository.findAllById(idList);
    }

    /**
     * @param
     * @return
     */
    public List<Entity> all() {
        return this.jpaRepository.findAll();
    }


    /**
     * @param <KEY>
     * @param idList
     * @param group
     * @return
     */
    public <KEY> Map<KEY, List<Entity>> map(final List<ID> idList, final Function<Entity, KEY> group) {
        return this.all(idList)
                   .stream()
                   .collect(Collectors.groupingBy(group));
    }

    /**
     * @param <KEY>
     * @param idList
     * @return
     */
    public <KEY> Map<KEY, Entity> mapDistinct(final List<ID> idList, final Function<Entity, KEY> distinct) {
        return this.all(idList)
                   .stream()
                   .collect(Collectors.toMap(distinct, Function.identity()));
    }


    /**
     * 列表
     *
     * @param request
     * @return
     */
    public <T> List<T> list(final PageRequest request, final Function<Entity, T> fun) {
        return this.pageRepository.list(this.entityClass, QueryFactory.build(request), request.getPage())
                                  .stream()
                                  .map(fun)
                                  .collect(Collectors.toList());
    }


    /**
     * 列表
     *
     * @param request
     * @return
     */
    public List<Entity> list(final PageRequest request) {
        return this.pageRepository.list(this.entityClass, QueryFactory.build(request), request.getPage());
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
            this.jpaRepository.deleteById(id);
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
        final Optional<Entity> one = this.jpaRepository.findById(id);
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
            return this.jpaRepository.findById(id)
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
        final Optional<Entity> one = this.jpaSpecificationExecutor.findOne(QueryFactory.build(request));
        if (!one.isPresent()) {
            throw new SystemException("000009");
        }
        return one.get();
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
        return this.jpaRepository.save(m);
    }

    /**
     * @param id
     * @return
     */
    public boolean exist(final ID id) {
        return this.jpaRepository.existsById(id);
    }

    /**
     * @param query
     * @return
     */
    public boolean exist(final PredicateBuilder query) {
        return this.jpaSpecificationExecutor.count(query.to()) > 0;
    }

    /**
     * @param query
     * @return
     */
    public Long count(final PredicateBuilder query) {
        return this.jpaSpecificationExecutor.count(query);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }


}
