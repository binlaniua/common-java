package com.github.binlaniua.common.json;

import com.alibaba.fastjson.parser.ParserConfig;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonConfig implements InitializingBean {

    @Autowired
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        return objectMapper;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ParserConfig.getGlobalInstance()
                    .setAutoTypeSupport(true);
    }
}
