package com.jd.blockchain;
/**
 * @Author zhaogw
 * @Date 2018/11/26 20:46
 */
public abstract class StringUtils {
    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }
}