package com.jd.blockchain.contract.maven;

import com.jd.blockchain.contract.maven.rule.BlackList;
import com.jd.blockchain.contract.maven.rule.WhiteList;

import java.util.List;

public abstract class AbstractContract {

    protected String className;

    public String getClassName() {
        return className;
    }

    public String getDotClassName() {
        return className.replaceAll("/", ".");
    }

    protected String format(final String inputFormat) {
        String formatResult;

        String outputFormat = inputFormat;
        if (inputFormat.endsWith(";")) {
            outputFormat = inputFormat.substring(0, inputFormat.length() - 1);
        }
        if (outputFormat.startsWith("[L") && outputFormat.length() > 2) {
            // 说明是数组，但不显示
            formatResult = outputFormat.substring(2);
        } else if (outputFormat.startsWith("[") && outputFormat.length() > 1) {
            // 说明是数组
            formatResult = outputFormat.substring(1);
        } else if (outputFormat.startsWith("L") && outputFormat.length() > 1) {
            // 说明是非基础类型
            formatResult = outputFormat.substring(1);
        } else {
            formatResult = outputFormat;
        }

        return formatResult;
    }

    public static BlackList initBlack(List<String> blackList) {
        BlackList contractBlack = new BlackList();
        if (blackList != null && !blackList.isEmpty()) {
            for (String black : blackList) {
                // 首先判断该black是package还是
                String packageName = isPackageAndReturn(black);
                if (packageName != null) {
                    // 说明是包
                    contractBlack.addBlackPackage(packageName);
                } else {
                    String[] classAndMethod = black.split("-");
                    if (classAndMethod.length == 1) {
                        // 说明只有ClassName
                        contractBlack.addBlack(classAndMethod[0], BlackList.COMMON_METHOD);
                    } else {
                        contractBlack.addBlack(classAndMethod[0], classAndMethod[1]);
                    }
                }
            }
        }

        return contractBlack;
    }

    public static WhiteList initWhite(List<String> whiteList) {
        WhiteList contractWhite = new WhiteList();

        if (whiteList != null && !whiteList.isEmpty()) {
            for (String white : whiteList) {
                String packageName = isPackageAndReturn(white);
                if (packageName != null) {
                    // 说明是包
                    contractWhite.addWhite(packageName);
                } else {
                    contractWhite.addWhite(white);
                }
            }
        }

        return contractWhite;
    }

    /**
     * 获取配置的packageName
     *
     * @param config
     * @return
     *     假设为包，则返回其包名，否则返回NULL
     */
    public static String isPackageAndReturn(String config) {
        if (config.endsWith("*")) {
            return config.substring(0, config.length() - 2);
        }
        return null;
    }
}
