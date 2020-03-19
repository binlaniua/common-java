package com.github.binlaniua.common.http.service.result;


import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.StopWatch;

/**
 * Tkk
 */
public interface ResultExecute<T> {

    T toBody(Response response, Request request, StopWatch stopWatch) throws Exception;
}
