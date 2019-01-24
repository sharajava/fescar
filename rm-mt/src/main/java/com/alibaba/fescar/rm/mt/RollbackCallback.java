package com.alibaba.fescar.rm.mt;

public interface RollbackCallback {

    void rollback() throws Throwable;
}
