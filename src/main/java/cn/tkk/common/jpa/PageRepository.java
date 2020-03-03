package cn.tkk.common.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kun Tang on 2018/11/7.
 */
@Repository
public class PageRepository {

    @Autowired
    EntityManager entityManager;

    public static class SimpleJpaNoCountRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> {
        public SimpleJpaNoCountRepository(final Class<T> domainClass, final EntityManager em) {
            super(domainClass, em);
        }

        @Override
        protected <S extends T> Page<S> readPage(final TypedQuery<S> query, final Class<S> domainClass, final Pageable pageable, final Specification<S> spec) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
            final List<S> content = query.getResultList();
            return new PageImpl<>(content, pageable, content.size());
        }
    }

    private static final Map<Class, SimpleJpaRepository> repositoryMap = new ConcurrentHashMap<>();

    public <T> List<T> list(final Class<T> domainClass, final Specification<T> spec, final Pageable pageable) {
        final Page all = this.getRepository(domainClass)
                             .findAll(spec, pageable);
        return all.getContent();
    }

    private SimpleJpaRepository getRepository(final Class domain) {
        return repositoryMap.computeIfAbsent(domain, (d) -> {
            final SimpleJpaRepository simpleJpaRepository = new SimpleJpaNoCountRepository(domain, this.entityManager);
            repositoryMap.put(domain, simpleJpaRepository);
            return simpleJpaRepository;
        });
    }
}
