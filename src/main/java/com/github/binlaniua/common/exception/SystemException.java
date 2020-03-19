package com.github.binlaniua.common.exception;

import io.jsonwebtoken.lang.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Tkk
 */
@Getter
@Setter
public class SystemException extends RuntimeException {

    private String errorCode;

    private Object[] params;

    public SystemException() {
        super();
        this.errorCode = "KK-000001";
    }

    public SystemException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public SystemException(String errorCode, Object... params) {
        this.errorCode = errorCode;
        this.params = params;
    }

    public SystemException(String errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }

    public SystemException(String errorCode, Object[] params, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.params = params;
    }

    @Override
    public String toString() {
        return errorCode + " => " + Objects.nullSafeToString(params);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
    }
}
