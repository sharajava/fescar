package com.alibaba.fescar.rm.mt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fescar.common.util.StringUtils;

public class BranchUtils {

    public static String encode(Object[] objects) {
        if (objects == null || objects.length == 0) { return null; }
        return JSON.toJSONString(objects);
    }


    public static Object[] decode(String branchKey) {
        if (StringUtils.isEmpty(branchKey)) { return null; }
        return JSON.parseObject(branchKey, Object[].class);
    }
}
