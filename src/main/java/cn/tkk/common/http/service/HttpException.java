package cn.tkk.common.http.service;

import lombok.Getter;
import lombok.Setter;
import okhttp3.Request;
import org.springframework.util.StopWatch;

/**
 * Tkk
 */
@Setter
@Getter
public class HttpException extends RuntimeException {

    private Request request;

    private StopWatch stopWatch;

    public HttpException() {
    }

    public HttpException(final String message) {
        super(message);
    }

    public HttpException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public HttpException(final Throwable cause) {
        super(cause);
    }

    public HttpException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public static HttpException make(final String msg, final Request req, final StopWatch s) {
        final HttpException exception = new HttpException(msg);
        exception.setRequest(req);
        exception.setStopWatch(s);
        return exception;
    }

    public static HttpException make(final Exception e, final Request req, final StopWatch s) {
        final HttpException exception = new HttpException(e);
        exception.setRequest(req);
        exception.setStopWatch(s);
        return exception;
    }

}
