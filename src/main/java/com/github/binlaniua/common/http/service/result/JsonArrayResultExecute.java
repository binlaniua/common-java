package com.github.binlaniua.common.http.service.result;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.List;

/**
 * Tkk
 */
@Data
@Builder
public class JsonArrayResultExecute<T>  {

    private final StringResultExecute stringResultExecute = new StringResultExecute();

    private Class<T> clazz;

    public <T> List<T> toBody(Response response, Request request, StopWatch stopWatch) throws IOException {
        String body = stringResultExecute.toBody(response, request, stopWatch);
        return JSON.parseArray(body, (Class<T>) clazz);
    }
}
