package com.jd.blockchain.mocker.proxy;

import java.lang.reflect.Method;

public class ExecutorProxy {

    private Object instance;

    private Method method;

    private Object[] args;

    public ExecutorProxy(Object instance, Method method, Object[] args) {
        this.instance = instance;
        this.method = method;
        this.args = args;
    }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object invoke() throws Exception {
        return method.invoke(instance, args);
    }
}
