package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.model.BranchStatus;

public class DefaultMTBranchManagerImpl implements MTBranchManager {

    private BranchCallback branchCallback;

    private String resourceId;

    public DefaultMTBranchManagerImpl(String branchName, String applicationId, String txServiceGroup, String env) {
        if (env == null) { env = "ENV"; }
        resourceId = env + "." + applicationId + "." + txServiceGroup + "." + branchName;
    }

    @Override
    public String prepare(String xid, long branchId) throws Exception {

        return null;
    }

    @Override
    public BranchStatus commit(String xid, long branchId, String applicationData) throws TransactionException {
        return null;
    }

    @Override
    public BranchStatus rollback(String xid, long branchId, String applicationData) throws TransactionException {
        return null;
    }

    @Override
    public String getResourceGroupId() {
        return "DEFAULT";
    }

    @Override
    public String getResourceId() {
        return resourceId;
    }
}
