package com.alibaba.fescar.rm.mt;

import com.alibaba.fescar.core.context.ContextCore;
import com.alibaba.fescar.core.context.ContextCoreLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BranchContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchContext.class);

    public static final String KEY_BRANCH_XID = "TX_BRANCH_XID";
    public static final String KEY_BRANCH_ID = "TX_BRANCH_ID";
    public static final String KEY_BRANCH_APPLICATION_DATA = "TX_BRANCH_APPLICATION_DATA";

    protected static ContextCore CONTEXT_HOLDER = ContextCoreLoader.load();

    public static void bind(String xid, Long branchId, String applicationData) {
        bindXID(xid);
        bindBranchId(branchId);
        bindApplicationData(applicationData);
    }

    public static void unbind() {
        unbindXID();
        unbindBranchId();
        unbindApplicationData();
    }

    public static String getXID() {
        return CONTEXT_HOLDER.get(KEY_BRANCH_XID);
    }

    public static void bindXID(String xid) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("bind XID " + xid);
        }
        CONTEXT_HOLDER.put(KEY_BRANCH_XID, xid);
    }

    public static String unbindXID() {
        String xid = CONTEXT_HOLDER.remove(KEY_BRANCH_XID);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("unbind XID " + xid);
        }
        return xid;
    }

    public static void bindBranchId(Long branchId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("bind branchId " + branchId);
        }
        CONTEXT_HOLDER.put(KEY_BRANCH_ID, branchId.toString());
    }

    public static Long unbindBranchId() {
        String v = CONTEXT_HOLDER.remove(KEY_BRANCH_ID);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("unbind branchId " + v);
        }
        return (v == null ? null : Long.parseLong(v));
    }

    public static Long getBranchId() {
        String v = CONTEXT_HOLDER.get(KEY_BRANCH_ID);
        return (v == null ? null : Long.parseLong(v));
    }

    public static void bindApplicationData(String applicationData) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("bind application data " + applicationData);
        }
        CONTEXT_HOLDER.put(KEY_BRANCH_APPLICATION_DATA, applicationData);
    }

    public static String unbindApplicationData() {
        String applicationData = CONTEXT_HOLDER.remove(KEY_BRANCH_APPLICATION_DATA);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("unbind application data " + applicationData);
        }
        return applicationData;
    }

    public static String getApplicationData() {
        return CONTEXT_HOLDER.get(KEY_BRANCH_APPLICATION_DATA);
    }

}
