package com.jd.blockchain.contract.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContractClass extends AbstractContract {

    // 若出现同名的方法则进行合并（将两个方法中涉及到的内容合并在一起）
    private Map<String, ContractMethod> methods = new ConcurrentHashMap<>();

    public ContractClass(String className) {
        if (className.contains(".")) {
            this.className = className.replaceAll("\\.", "/");
        } else {
            this.className = className;
        }
    }

    /**
     * 返回构造方法
     *
     * @return
     */
    public ContractMethod constructor() {
        return methods.get(ContractConstant.METHOD_INIT);
    }

    /**
     * 返回该类的所有变量
     *
     * @return
     */
    public List<ContractField> fields() {

        List<ContractField> fields = new ArrayList<>();

        // 构造方法
        ContractMethod initMethod = constructor();
        if (initMethod != null) {
            fields.addAll(initMethod.getClassFieldList(className));
        }
        // CLINIT方法
        ContractMethod clInitMethod = methods.get(ContractConstant.METHOD_CLINIT);
        if (clInitMethod != null) {
            fields.addAll(clInitMethod.getClassFieldList(className));
        }
        return fields;
    }

    public synchronized ContractMethod method(String methodName) {
        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }
        ContractMethod method = new ContractMethod(this.className, methodName);

        methods.put(methodName, method);

        return method;
    }

    public Map<String, ContractMethod> getMethods() {
        return methods;
    }
}
