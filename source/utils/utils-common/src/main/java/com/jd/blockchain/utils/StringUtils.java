package com.jd.blockchain.utils;

import java.util.regex.Pattern;

/**
 * @Author zhaogw
 * date 2018/11/26 20:46
 */
public class StringUtils {
    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */

    public static boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}