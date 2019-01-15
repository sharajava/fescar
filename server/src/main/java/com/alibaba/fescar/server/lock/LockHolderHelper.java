package com.alibaba.fescar.server.lock;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LockHolderHelper {

    public static boolean clean(ConcurrentHashMap<Map<String, Long>, Set<String>> lockHolder, long transactionId) {
        if (lockHolder.size() == 0) {
            return true;
        }
        Iterator<Map.Entry<Map<String, Long>, Set<String>>> it = lockHolder.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Map<String, Long>, Set<String>> entry = it.next();
            Map<String, Long> bucket = entry.getKey();
            Set<String> keys = entry.getValue();
            synchronized (bucket) {
                for (String key : keys) {
                    Long v = bucket.get(key);
                    if (v == null) {
                        continue;
                    }
                    if (v.longValue() == transactionId) {
                        bucket.remove(key);
                    }
                }
            }
        }
        lockHolder.clear();
        return true;
    }
}
