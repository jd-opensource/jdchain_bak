package com.jd.blockchain.contract.maven.rule;

import java.util.ArrayList;
import java.util.List;

public class WhiteList {

    // 合约白名单（白名单通常数量较少，主要是JDChain内部包）
    private final List<String> whiteClasses = new ArrayList<>();

    public void addWhite(String className) {
        whiteClasses.add(className.trim());
    }

    public boolean isWhite(Class<?> clazz) {
        String className = clazz.getName();
        return isWhite(className);
    }

    public boolean isWhite(String className) {
        for (String white : whiteClasses) {
            if (white.equals(className) || className.startsWith(white)) {
                return true;
            }
        }
        return false;
    }
}


