package com.github.binlaniua.common.jpa;

import com.github.binlaniua.common.jpa.generator.SnowflaskGenerator;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

/**
 * 主键生成策略
 * Tkk
 */
public class CustomIdentifierGenerator implements IdentifierGenerator, Configurable {

    private String type;
    private Properties props;
    private String entityName;

    private final SnowflaskGenerator snowflaskGenerator = new SnowflaskGenerator(0L, 0L);

    @Override
    public void configure(final Type type, final Properties params, final ServiceRegistry serviceRegistry) throws MappingException {
        this.type = params.get("type")
                          .toString()
                          .toLowerCase();
        this.props = params;
        this.entityName = params.getProperty(ENTITY_NAME);
    }

    public Serializable generate(final SessionImplementor session, final Object object) throws HibernateException {
        final Serializable id = session
                .getEntityPersister(this.entityName, object)
                .getIdentifier(object, session);
        if (id != null && StringUtils.isNotBlank(id.toString())) {
            return id;
        }
        switch (this.type) {
            case "number":
                return this.snowflaskGenerator.nextId();
            case "datetime":
                return DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
            default:
                final String uuid = UUID.randomUUID()
                                        .toString()
                                        .replaceAll("-", "");
                return uuid;
        }
    }

    @Override
    public Serializable generate(final SharedSessionContractImplementor session, final Object object) throws HibernateException {
        final Serializable id = session
                .getEntityPersister(this.entityName, object)
                .getIdentifier(object, session);
        if (id != null && StringUtils.isNotBlank(id.toString())) {
            return id;
        }
        switch (this.type) {
            case "number":
                return this.snowflaskGenerator.nextId();
            case "datetime":
                return DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
            default:
                final String uuid = UUID.randomUUID()
                                        .toString()
                                        .replaceAll("-", "");
                return uuid;
        }
    }
}
