package com.github.binlaniua.common.context;

/**
 *  Tkk
 */
public class RequestHolder {
    private static final ThreadLocal<Request> r = new ThreadLocal<>();

    public static Request get() {
        return r.get();
    }

    public static void set(Request request) {
        r.set(request);
    }

    public static void clean() {
        r.remove();
    }
}
