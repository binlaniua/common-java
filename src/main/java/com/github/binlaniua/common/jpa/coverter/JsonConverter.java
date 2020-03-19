package com.github.binlaniua.common.jpa.coverter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Tkk
 */
@Converter
public class JsonConverter implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        return attribute == null ? null : JSON.toJSONString(attribute, SerializerFeature.WriteClassName);
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        return StringUtils.isNotBlank(dbData) ? JSON.parse(dbData) : null;
    }
}
