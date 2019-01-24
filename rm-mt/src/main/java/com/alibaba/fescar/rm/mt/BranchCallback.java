package com.alibaba.fescar.rm.mt;

public interface BranchCallback {

    void prepare();

    void commit();

    void rollback();
}
