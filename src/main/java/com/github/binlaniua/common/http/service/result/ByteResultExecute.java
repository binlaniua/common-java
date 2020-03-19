package com.github.binlaniua.common.http.service.result;

import cn.tkk.common.http.service.HttpException;
import com.github.binlaniua.common.http.service.HttpException;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.util.StopWatch;

import java.io.IOException;

/**
 * Tkk
 */
public class ByteResultExecute implements ResultExecute<byte[]> {


    @Override
    public byte[] toBody(final Response response, final Request request, final StopWatch stopWatch) throws IOException {
        switch (response.code()) {
            case 200:
                return response.body()
                               .bytes();
            default:
                throw HttpException.make(response.code() + " ==> " + response.body()
                                                                             .string(), request, stopWatch);
        }
    }
}
