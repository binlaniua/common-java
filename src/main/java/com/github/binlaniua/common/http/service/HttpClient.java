package com.github.binlaniua.common.http.service;

import com.github.binlaniua.common.http.service.result.ResultExecute;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * Tkk
 */
@Component
public class HttpClient {

    @Autowired
    private OkHttpClient client;

    public <T> T execute(final HttpParams params, final ResultExecute resultExecute) {
        final Request request = params.toRequest();
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Response response = null;
        try {
            response = this.client.newCall(request)
                                  .execute();
            stopWatch.stop();
            return (T) resultExecute.toBody(response, request, stopWatch);
        } catch (final HttpException e) {
            throw e;
        } catch (final Exception e) {
            throw HttpException.make(e, request, stopWatch);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
