package com.alibaba.fescar.rm.mt.api;

import com.alibaba.fescar.core.context.RootContext;
import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.core.model.BranchType;
import com.alibaba.fescar.core.model.Resource;
import com.alibaba.fescar.rm.mt.ManualBranchManager;

public class ManualBranchHelper {

    public static Long branchRegister(Resource resource, String branchKey) throws TransactionException {
        return ManualBranchManager.get().branchRegister(BranchType.MT, resource.getResourceId(), null, RootContext.getXID(),
            null, branchKey);
    }

    public static void branchReport(long branchId, BranchStatus status, String applicationData) throws TransactionException {
        ManualBranchManager.get().branchReport(RootContext.getXID(), branchId, status, applicationData);
    }


}
