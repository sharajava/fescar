package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.core.model.Resource;

public interface MTBranchManager extends Resource {

    String prepare(String xid, long branchId) throws Exception;

    BranchStatus commit(String xid, long branchId, String applicationData) throws TransactionException;

    BranchStatus rollback(String xid, long branchId, String applicationData) throws TransactionException;

}
