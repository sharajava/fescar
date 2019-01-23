package com.alibaba.fescar.rm.mt;

public interface BranchCallback {

    void prepare(BranchContext branchContext);

    void commit(BranchContext branchContext);

    void rollback(BranchContext branchContext);

}
