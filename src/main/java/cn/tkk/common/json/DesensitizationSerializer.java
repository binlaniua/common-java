package cn.tkk.common.json;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

/**
 * 脱敏序列号
 * Created by Kun Tang on 2018/10/28.
 */
public class DesensitizationSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if (StringUtils.isBlank(s)) {
            jsonGenerator.writeString(s);
            return;
        }
        jsonGenerator.writeString(
                StringUtils.substring(s, 0, 3) +
                        StringUtils.substring(s, 3, s.length() - 3).replaceAll(".", "*") +
                        StringUtils.substring(s, s.length() - 3)
        );
    }
}
