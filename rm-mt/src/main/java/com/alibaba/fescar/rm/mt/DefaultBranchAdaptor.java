package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.core.exception.TransactionException;
import com.alibaba.fescar.core.exception.TransactionExceptionCode;
import com.alibaba.fescar.core.model.BranchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBranchAdaptor implements PhaseTwo, PhaseOne {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBranchAdaptor.class);

    private String branchName;

    private PrepareCallback prepareCallback;

    private CommitCallback commitCallback;

    private RollbackCallback rollbackCallback;

    public DefaultBranchAdaptor(String branchName, PrepareCallback prepareCallback) {
        this.branchName = branchName;
        this.prepareCallback = prepareCallback;
    }

    public DefaultBranchAdaptor(String branchName, CommitCallback commitCallback, RollbackCallback rollbackCallback) {
        this.branchName = branchName;
        this.commitCallback = commitCallback;
        this.rollbackCallback = rollbackCallback;
    }

    @Override
    public String prepare(String xid, long branchId) throws TransactionException {
        String applicationData = null;
        try {
            BranchContext.bind(xid, branchId, null);

            prepareCallback.prepare();

            applicationData = BranchContext.getApplicationData();

        } catch (Throwable ex) {
            if (ex instanceof TransactionException) {
                throw (TransactionException) ex;
            } else {
                throw new TransactionException(ex);
            }

        } finally {
            BranchContext.unbind();
        }

        return applicationData;
    }

    @Override
    public BranchStatus commit(String xid, long branchId, String applicationData) throws TransactionException {
        try {
            BranchContext.bind(xid, branchId, applicationData);

            commitCallback.commit();
            return BranchStatus.PhaseTwo_Committed;

        } catch (Throwable ex) {
            if (ex instanceof TransactionException) {
                TransactionException tex = (TransactionException) ex;
                if (tex.getCode() == TransactionExceptionCode.BranchCommitFailed_Unretriable) {
                    return BranchStatus.PhaseTwo_CommitFailed_Unretryable;
                } else if (tex.getCode() == TransactionExceptionCode.BranchCommitFailed_Retriable) {
                    return BranchStatus.PhaseTwo_CommitFailed_Retryable;
                } else {
                    throw tex;
                }
            } else {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Branch Commit Failed!", ex);
                }
                return BranchStatus.PhaseTwo_CommitFailed_Retryable;
            }

        } finally {
            BranchContext.unbind();
        }
    }

    @Override
    public BranchStatus rollback(String xid, long branchId, String applicationData) throws TransactionException {
        try {
            BranchContext.bind(xid, branchId, applicationData);

            rollbackCallback.rollback();
            return BranchStatus.PhaseTwo_Rollbacked;

        } catch (Throwable ex) {
            if (ex instanceof TransactionException) {
                TransactionException tex = (TransactionException) ex;
                if (tex.getCode() == TransactionExceptionCode.BranchRollbackFailed_Unretriable) {
                    return BranchStatus.PhaseTwo_RollbackFailed_Unretryable;
                } else if (tex.getCode() == TransactionExceptionCode.BranchRollbackFailed_Retriable) {
                    return BranchStatus.PhaseTwo_RollbackFailed_Retryable;
                } else {
                    throw tex;
                }
            } else {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Branch Rollback Failed!", ex);
                }
                return BranchStatus.PhaseTwo_RollbackFailed_Retryable;
            }

        } finally {
            BranchContext.unbind();
        }
    }

    @Override
    public String getResourceGroupId() {
        return "DEFAULT";
    }

    @Override
    public String getResourceId() {
        return branchName;
    }
}
