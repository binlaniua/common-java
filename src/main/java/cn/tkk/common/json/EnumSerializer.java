package cn.tkk.common.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumSerializer extends StdSerializer {

    public static final String GET = "get";

    private static final class MethodDescriptor {
        private Method method;
        private String fieldName;
    }

    private static final Map<Class, MethodDescriptor[]> CLASS_MAP = new HashMap<>();

    public EnumSerializer() {
        super(Object.class);
    }

    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // 写入枚举本身的值
        Enum e = (Enum) o;
        jsonGenerator.writeString(e.name());

        // 获取前缀
        String prefix = jsonGenerator.getOutputContext()
                                     .getCurrentName();
        if (prefix == null) {
            return;
        }

        // 获取枚举其他属性, 获取输出
        for (MethodDescriptor methodDescriptor : CLASS_MAP.computeIfAbsent(o.getClass(), EnumSerializer::description)) {
            jsonGenerator.writeObjectField(
                    prefix + methodDescriptor.fieldName,
                    ReflectionUtils.invokeMethod(methodDescriptor.method, o)
            );
        }
    }

    private static synchronized MethodDescriptor[] description(Class aClass) {
        if (CLASS_MAP.containsKey(aClass)) {
            return CLASS_MAP.get(aClass);
        }
        List<MethodDescriptor> result = new ArrayList<>(aClass.getMethods().length);
        ReflectionUtils.doWithLocalMethods(aClass, method -> {
            if (!StringUtils.startsWith(method.getName(), GET)) {
                return;
            }
            MethodDescriptor methodDescriptor = new MethodDescriptor();
            methodDescriptor.method = method;
            methodDescriptor.fieldName = (StringUtils.substringAfter(method.getName(), GET));
            result.add(methodDescriptor);
        });
        return result.toArray(new MethodDescriptor[result.size()]);
    }


}
