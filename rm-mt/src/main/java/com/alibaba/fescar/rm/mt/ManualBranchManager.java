package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.common.XID;
import com.alibaba.fescar.common.exception.NotSupportYetException;
import com.alibaba.fescar.common.exception.ShouldNeverHappenException;
import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.exception.TransactionExceptionCode;
import com.alibaba.fescar.core.model.BranchStatus;
import com.alibaba.fescar.core.model.BranchType;
import com.alibaba.fescar.core.model.Resource;
import com.alibaba.fescar.core.model.ResourceManager;
import com.alibaba.fescar.core.protocol.ResultCode;
import com.alibaba.fescar.core.protocol.transaction.*;
import com.alibaba.fescar.core.rpc.netty.RmRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

public class ManualBranchManager implements ResourceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManualBranchManager.class);

    private Map<String, Resource> managedResources = new ConcurrentHashMap<>();

    private static class SingletonHolder {
        private static ManualBranchManager INSTANCE = new ManualBranchManager();
    }

    public static ManualBranchManager get() {
        return SingletonHolder.INSTANCE;
    }

    public static void set(ManualBranchManager mock) {
        SingletonHolder.INSTANCE = mock;
    }


    private BranchType getBranchType() {
        return BranchType.MT;
    }

    @Override
    public void registerResource(Resource resource) {
        RmRpcClient.getInstance().registerResource(resource.getResourceGroupId(), resource.getResourceId());
        managedResources.put(resource.getResourceId(), resource);

    }

    @Override
    public void unregisterResource(Resource resource) {
        throw new NotSupportYetException("unregister a resource");

    }

    @Override
    public Map<String, Resource> getManagedResources() {
        return managedResources;
    }

    @Override
    public BranchStatus branchCommit(String xid, long branchId, String resourceId, String branchKey, String applicationData)
        throws TransactionException {
        Resource resource = managedResources.get(resourceId);
        if (resource == null) {
            throw new ShouldNeverHappenException();
        }
        PhaseTwo phaseTwo = (PhaseTwo)resource;
        try {
            phaseTwo.commit(xid, branchId, branchKey, applicationData);
            return BranchStatus.PhaseTwo_Committed;
        } catch (Throwable ex) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("MT Branch Commit Failed " + xid + " " + branchId);
            }
            if (ex instanceof TransactionException) {
                TransactionException te = (TransactionException)ex;
                if (te.getCode() == TransactionExceptionCode.BranchCommitFailed_Unretriable) {
                    return BranchStatus.PhaseTwo_CommitFailed_Unretryable;
                } else {
                    return BranchStatus.PhaseTwo_CommitFailed_Retryable;
                }
            }
            return BranchStatus.PhaseTwo_CommitFailed_Retryable;
        }
    }

    @Override
    public BranchStatus branchCommit(String xid, long branchId, String resourceId, String applicationData)
        throws TransactionException {
        return branchCommit(xid, branchId, resourceId, null, applicationData);
    }

    @Override
    public BranchStatus branchRollback(String xid, long branchId, String resourceId, String branchKey, String applicationData)
        throws TransactionException {
        Resource resource = managedResources.get(resourceId);
        if (resource == null) {
            throw new ShouldNeverHappenException();
        }
        PhaseTwo phaseTwo = (PhaseTwo)resource;
        try {
            phaseTwo.rollback(xid, branchId, branchKey, applicationData);
            return BranchStatus.PhaseTwo_Rollbacked;
        } catch (Throwable ex) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("MT Branch Commit Failed " + xid + " " + branchId);
            }
            if (ex instanceof TransactionException) {
                TransactionException te = (TransactionException)ex;
                if (te.getCode() == TransactionExceptionCode.BranchRollbackFailed_Unretriable) {
                    return BranchStatus.PhaseTwo_RollbackFailed_Unretryable;
                } else {
                    return BranchStatus.PhaseTwo_RollbackFailed_Retryable;
                }
            }
            return BranchStatus.PhaseTwo_RollbackFailed_Retryable;
        }
    }

    @Override
    public BranchStatus branchRollback(String xid, long branchId, String resourceId, String applicationData)
        throws TransactionException {
        return branchRollback(xid, branchId, resourceId, null, applicationData);
    }

    @Override
    public Long branchRegister(BranchType branchType, String resourceId, String clientId, String xid, String lockKeys)
        throws TransactionException {
        return branchRegister(branchType, resourceId, clientId, xid, lockKeys, null);
    }

    @Override
    public Long branchRegister(BranchType branchType, String resourceId, String clientId, String xid, String lockKeys,
                               String branchKey)
        throws TransactionException {
        try {
            BranchRegisterRequest request = new BranchRegisterRequest();
            request.setTransactionId(XID.getTransactionId(xid));
            request.setLockKey(lockKeys);
            request.setResourceId(resourceId);
            request.setBranchType(getBranchType());

            BranchRegisterResponse response = (BranchRegisterResponse) RmRpcClient.getInstance().sendMsgWithResponse(request);
            if (response.getResultCode() == ResultCode.Failed) {
                throw new TransactionException(response.getTransactionExceptionCode(), "Response[" + response.getMsg() + "]");
            }
            return response.getBranchId();
        } catch (TimeoutException toe) {
            throw new TransactionException(TransactionExceptionCode.IO, "RPC Timeout", toe);
        } catch (RuntimeException rex) {
            throw new TransactionException(TransactionExceptionCode.BranchRegisterFailed, "Runtime", rex);
        }
    }

    @Override
    public void branchReport(String xid, long branchId, BranchStatus status, String applicationData)
        throws TransactionException {
        try {
            BranchReportRequest request = new BranchReportRequest();
            request.setTransactionId(XID.getTransactionId(xid));
            request.setBranchId(branchId);
            request.setStatus(status);
            request.setApplicationData(applicationData);

            BranchReportResponse response = (BranchReportResponse) RmRpcClient.getInstance().sendMsgWithResponse(request);
            if (response.getResultCode() == ResultCode.Failed) {
                throw new TransactionException(response.getTransactionExceptionCode(), "Response[" + response.getMsg() + "]");
            }
        } catch (TimeoutException toe) {
            throw new TransactionException(TransactionExceptionCode.IO, "RPC Timeout", toe);
        } catch (RuntimeException rex) {
            throw new TransactionException(TransactionExceptionCode.BranchReportFailed, "Runtime", rex);
        }
    }

    @Override
    public boolean lockQuery(BranchType branchType, String resourceId, String xid, String lockKeys)
        throws TransactionException {
        try {
            GlobalLockQueryRequest request = new GlobalLockQueryRequest();
            request.setTransactionId(XID.getTransactionId(xid));
            request.setLockKey(lockKeys);
            request.setResourceId(resourceId);

            GlobalLockQueryResponse response = (GlobalLockQueryResponse) RmRpcClient.getInstance().sendMsgWithResponse(request);
            if (response.getResultCode() == ResultCode.Failed) {
                throw new TransactionException(response.getTransactionExceptionCode(), "Response[" + response.getMsg() + "]");
            }
            return response.isLockable();
        } catch (TimeoutException toe) {
            throw new TransactionException(TransactionExceptionCode.IO, "RPC Timeout", toe);
        } catch (RuntimeException rex) {
            throw new TransactionException(TransactionExceptionCode.LockableCheckFailed, "Runtime", rex);
        }
    }
}
