package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.core.model.Resource;

public interface PhaseTwo extends Resource {

    void commit(String xid, long branchId, String branchKey, String applicationData) throws Throwable;

    void rollback(String xid, long branchId, String branchKey, String applicationData) throws Throwable;

}
