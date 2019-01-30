package com.alibaba.fescar.rm.mt.tcc;

public class MyBusinessException extends Exception {

    public MyBusinessException() {
        super();
    }

    public MyBusinessException(String message) {
        super(message);
    }

    public MyBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyBusinessException(Throwable cause) {
        super(cause);
    }

    protected MyBusinessException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
