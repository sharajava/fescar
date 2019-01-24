package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.core.model.Resource;

public interface PhaseTwo extends Resource {

    BranchStatus commit(String xid, long branchId, String applicationData) throws TransactionException;

    BranchStatus rollback(String xid, long branchId, String applicationData) throws TransactionException;

}
