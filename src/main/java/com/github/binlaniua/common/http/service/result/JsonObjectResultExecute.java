package com.github.binlaniua.common.http.service.result;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.StopWatch;

import java.io.IOException;

/**
 * Tkk
 */
@Data
@Builder
public class JsonObjectResultExecute<T>  {

    private final StringResultExecute stringResultExecute = new StringResultExecute();

    private Class<T> clazz;

    public <T> T toBody(Response response, Request request, StopWatch stopWatch) throws IOException {
        String body = stringResultExecute.toBody(response, request, stopWatch);
        return JSON.parseObject(body, (Class<T>) clazz);
    }
}
