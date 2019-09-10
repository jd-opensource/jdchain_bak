package com.jd.blockchain.contract.maven.rule;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlackList {

    public static final String COMMON_METHOD = "*";

    public static final String INIT_METHOD = "init";

    // 合约黑名单
    private final Map<String, BlackClass> blackClassMap = new ConcurrentHashMap<>();

    private final List<String> blackPackages = new ArrayList<>();

    public synchronized BlackList addBlack(String className, String methodName) {
        String trimClassName = className.trim();
        BlackClass blackClass = blackClassMap.get(trimClassName);
        if (blackClass != null) {
            blackClass.addMethod(methodName);
        } else {
            blackClass = new BlackClass(trimClassName);
            blackClass.addMethod(methodName);
            blackClassMap.put(trimClassName, blackClass);
        }
        return this;
    }

    public synchronized BlackList addBlack(Class<?> clazz, String methodName) {
        return addBlack(clazz.getName(), methodName);
    }

    public synchronized BlackList addBlack(Class<?> clazz) {
        return addBlack(clazz.getName(), COMMON_METHOD);
    }

    public synchronized BlackList addBlackPackage(String packageName) {
        blackPackages.add(packageName.trim() + "."); // 末尾增加一个点，防止后续判断是拼凑
        return this;
    }

    public boolean isBlackClass(String className) {
        if (isContainsPackage(className)) {
            return true;
        }
        BlackClass blackClass = blackClassMap.get(className);
        if (blackClass == null) {
            return false;
        }
        return blackClass.isBlack();
    }

    public boolean isBlack(Class<?> clazz, String methodName) {

        // 判断该Class是否属于黑名单
        if (isCurrentClassBlack(clazz, methodName)) {
            return true;
        }
        // 当前Class不是黑名单的情况下，处理其对应的父类和接口
        // 获取该Class对应的接口和父类列表
        Set<Class<?>> superClassAndAllInterfaces = new HashSet<>();

        loadSuperClassAndAllInterfaces(clazz, superClassAndAllInterfaces);

        // 循环判断每个父类和接口
        for (Class<?> currClass : superClassAndAllInterfaces) {
            if (isCurrentClassBlack(currClass, methodName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCurrentClassBlack(Class<?> clazz, String methodName) {

        String packageName = clazz.getPackage().getName();
        for (String bp : blackPackages) {
            if ((packageName + ".").equals(bp) || packageName.startsWith(bp)) {
                return true;
            }
        }
        // 判断该类本身是否属于黑名单
        String className = clazz.getName();
        BlackClass blackClass = blackClassMap.get(className);
        if (blackClass != null) {
            // 判断其方法
            return blackClass.isBlack(methodName);
        }
        return false;
    }

    public boolean isBlackField(Class<?> clazz) {
        return isBlack(clazz, INIT_METHOD);
    }

    private boolean isContainsPackage(String className) {
        for (String bp : blackPackages) {
            if (className.equals(bp) || className.startsWith(bp)) {
                return true;
            }
        }
        return false;
    }

    private void loadSuperClassAndAllInterfaces(Class<?> currentClass, Set<Class<?>> allClassList) {
        if (currentClass == null) {
            return;
        }

        if (!allClassList.contains(currentClass)) {
            allClassList.add(currentClass);
            // 处理其父类
            Class<?> superClass = currentClass.getSuperclass();
            loadSuperClassAndAllInterfaces(superClass, allClassList);

            // 处理其所有接口
            Class<?>[] allInterfaces = currentClass.getInterfaces();
            if (allInterfaces != null && allInterfaces.length > 0) {
                for (Class<?> intf : allInterfaces) {
                    loadSuperClassAndAllInterfaces(intf, allClassList);
                }
            }
        }
    }

    private static class BlackClass {

        String className;

        Set<String> methods = new HashSet<>();

        BlackClass(String className) {
            this.className = className;
        }

        void addMethod(String methodName) {
            methods.add(methodName);
        }

        boolean isBlack(String methodName) {
            // 假设method为*则表示所有的方法
            if (methods.contains(COMMON_METHOD)) {
                return true;
            }
            return methods.contains(methodName);
        }

        boolean isBlack() {
            return isBlack(COMMON_METHOD);
        }
    }
}


