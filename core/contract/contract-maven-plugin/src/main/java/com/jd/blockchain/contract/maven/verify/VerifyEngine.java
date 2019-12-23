package com.jd.blockchain.contract.maven.verify;

import com.jd.blockchain.contract.ContractJarUtils;
import com.jd.blockchain.contract.maven.ContractClass;
import com.jd.blockchain.contract.maven.ContractField;
import com.jd.blockchain.contract.maven.ContractMethod;
import com.jd.blockchain.contract.maven.asm.ASMClassVisitor;
import com.jd.blockchain.contract.maven.rule.BlackList;
import com.jd.blockchain.contract.maven.rule.WhiteList;
import org.apache.maven.plugin.logging.Log;
import org.objectweb.asm.ClassReader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.jd.blockchain.contract.ContractJarUtils.loadAllClasses;


public class VerifyEngine {

    private Log LOGGER;

    private File jarFile;

    private String mainClass;

    private BlackList black;

    private WhiteList white;

    // 代表的只是当前方法，不代表该方法中的内部方法
    private Set<String> haveManagedMethods = new HashSet<>();

    // 代表的是处理的参数
    private Set<String> haveManagedFields = new HashSet<>();

    public VerifyEngine(Log LOGGER, File jarFile, String mainClass, BlackList black, WhiteList white) {
        this.LOGGER = LOGGER;
        this.jarFile = jarFile;
        this.mainClass = mainClass;
        this.black = black;
        this.white = white;
    }

    public void verify() throws Exception {
        // 加载所有的jar，然后ASM获取MAP
        URL jarURL = jarFile.toURI().toURL();

        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{jarURL},
                Thread.currentThread().getContextClassLoader());

        // 解析Jar包中所有的Class
        Map<String, ContractClass> allContractClasses = resolveClasses(jarClasses());

        // 开始处理MainClass
        verify(urlClassLoader, allContractClasses);

        // 校验完成后需要释放ClassLoader，否则无法删除该Jar包
        urlClassLoader.close();
    }

    public void verify(URLClassLoader urlClassLoader, Map<String, ContractClass> allContractClasses) throws Exception {
        // 获取MainClass
        String mainClassKey = convertClassKey(mainClass);
        ContractClass mainContractClass = allContractClasses.get(mainClassKey);
        if (mainContractClass == null) {
            LOGGER.error(String.format("Load Main Class = [%s] NULL !!!", mainClass));
            throw new IllegalStateException("MainClass is NULL !!!");
        }
        // 校验该Class中所有方法
        Map<String, ContractMethod> methods = mainContractClass.getMethods();
        if (!methods.isEmpty()) {
            for (Map.Entry<String, ContractMethod> entry : methods.entrySet()) {
                ContractMethod method = entry.getValue();
                verify(urlClassLoader, allContractClasses, method);
            }
        }
    }

    public void verify(URLClassLoader urlClassLoader, Map<String, ContractClass> allContractClasses, ContractMethod method) throws Exception {
        // 获取方法中涉及到的所有的Class及Method
        // 首先判断该方法对应的Class是否由urlClassLoader加载
        // 首先判断该ClassName对应方法是否处理过
        String managedKey = managedKey(method);
        if (haveManagedMethods.contains(managedKey)) {
            return;
        }
        // 将该方法设置为已处理
        haveManagedMethods.add(managedKey);
        String dotClassName = method.getDotClassName();


        Class<?> dotClass = urlClassLoader.loadClass(dotClassName);

        if (dotClass == null) {
            return;
        }
        String dotClassLoader = null;
        ClassLoader classLoader = dotClass.getClassLoader();

        if (classLoader != null) {
            dotClassLoader = dotClass.getClassLoader().toString();
        }

        if (dotClassLoader != null && dotClassLoader.contains("URLClassLoader")) {

            // 说明是URLClassLoader，这个需要先从黑名单和白名单列表中操作
            // 首先判断是否是黑名单，黑名单优先级最高
            if (black.isBlack(dotClass, method.getMethodName())) {
                throw new IllegalStateException(String.format("Class [%s] Method [%s] is Black !!!", dotClassName, method.getMethodName()));
            } else {
                // 不是黑名单的情况下，判断是否为白名单
                if (white.isWhite(dotClass)) {
                    return;
                }
                // 如果不属于白名单，则需要判断其子方法
                List<ContractMethod> innerMethods = method.getMethodList();
                if (!innerMethods.isEmpty()) {
                    for (ContractMethod innerMethod : innerMethods) {
                        // 需要重新从AllMap中获取，因为生成时并未处理其关联关系
                        ContractClass innerClass = allContractClasses.get(innerMethod.getClassName());
                        if (innerClass != null) {
                            ContractMethod verifyMethod = innerClass.method(innerMethod.getMethodName());
                            verify(urlClassLoader, allContractClasses, verifyMethod);
                        } else {
                            verify(urlClassLoader, allContractClasses, innerMethod);
                        }
                    }
                }
                List<ContractField> innerFields = method.getAllFieldList();
                if (!innerFields.isEmpty()) {
                    for (ContractField innerField : innerFields) {
                        verify(urlClassLoader, innerField);
                    }
                }
            }
        } else {
            // 非URLClassLoader加载的类，只需要做判断即可
            // 对于系统加载的类，其白名单优先级高于黑名单
            // 1、不再需要获取其方法；
            // 首先判断是否为白名单
            if (white.isWhite(dotClass)) {
                return;
            }
            // 然后判断其是否为黑名单
            if (black.isBlack(dotClass, method.getMethodName())) {
                throw new IllegalStateException(String.format("Class [%s] Method [%s] is Black !!!", dotClassName, method.getMethodName()));
            }
        }
    }

    public void verify(URLClassLoader urlClassLoader, ContractField field) throws Exception {
        // 获取方法中涉及到的所有的Class及Method
        // 首先判断该方法对应的Class是否由urlClassLoader加载
        // 首先判断该ClassName对应方法是否处理过
        String managedKey = managedKey(field);
        if (haveManagedFields.contains(managedKey)) {
            return;
        }
        // 将该字段设置为已读
        haveManagedFields.add(managedKey);

        Class<?> dotClass = urlClassLoader.loadClass(field.getDotClassName());

        if (dotClass == null) {
            return;
        }

        if (black.isBlackField(dotClass)) {
            throw new IllegalStateException(String.format("Class [%s] Field [%s] is Black !!!", field.getDotClassName(), field.getFieldName()));
        }
    }

    private Map<String, byte[]> jarClasses() throws Exception {
        return loadAllClasses(jarFile);
    }

    private Map<String, ContractClass> resolveClasses(Map<String, byte[]> allClasses) {

        Map<String, ContractClass> allContractClasses = new ConcurrentHashMap<>();

        for (Map.Entry<String, byte[]> entry : allClasses.entrySet()) {
            byte[] classContent = entry.getValue();
            if (classContent == null || classContent.length == 0) {
                continue;
            }
            String className = entry.getKey().substring(0, entry.getKey().length() - 6);

            String dotClassName = ContractJarUtils.dotClassName(className);
            if (white.isWhite(dotClassName) || black.isBlackClass(dotClassName)) {
                continue;
            }

            ContractClass contractClass = new ContractClass(className);
            ClassReader cr = new ClassReader(classContent);
            cr.accept(new ASMClassVisitor(contractClass), ClassReader.SKIP_DEBUG);
            allContractClasses.put(className, contractClass);
        }
        return allContractClasses;
    }

    private String convertClassKey(final String classKey) {
        String newClassKey = classKey;
        if (classKey.endsWith(".class")) {
            newClassKey = classKey.substring(0, classKey.length() - 6);
        }
        newClassKey = newClassKey.replaceAll("\\.", "/");
        return newClassKey;
    }

    private String managedKey(ContractMethod method) {
        return method.getDotClassName() + "-" + method.getMethodName();
    }

    private String managedKey(ContractField field) {
        return field.getDotClassName() + "-<init>-" + field.getFieldName();
    }
}
