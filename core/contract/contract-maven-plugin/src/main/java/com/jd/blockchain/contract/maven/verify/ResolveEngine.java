package com.jd.blockchain.contract.maven.verify;

import com.alibaba.fastjson.JSON;
import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractJarUtils;
import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.contract.EventProcessingAware;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.jd.blockchain.contract.ContractJarUtils.*;

public class ResolveEngine {

    private Log LOGGER;

//    private File jarFile;

    private String mainClass;

//    public ResolveEngine(Log LOGGER, File jarFile, String mainClass) {
    public ResolveEngine(Log LOGGER, String mainClass) {
        this.LOGGER = LOGGER;
//        this.jarFile = jarFile;
        this.mainClass = mainClass;
    }

    /**
     * 校验当前项目中MainClass其是否满足JDChain合约写法
     *
     * @param mainClassJarFile
     * @throws MojoFailureException
     */
    public void verifyCurrentProjectMainClass(File mainClassJarFile) throws MojoFailureException {
        // 校验MainClass
        try {
            LOGGER.debug(String.format("Verify Jar [%s] 's MainClass start...", mainClassJarFile.getName()));
            // 自定义ClassLoader，必须使用Thread.currentThread().getContextClassLoader()
            // 保证其项目内部加载的Jar包无须再加载一次
            URLClassLoader classLoader = new URLClassLoader(new URL[]{mainClassJarFile.toURI().toURL()},
                    Thread.currentThread().getContextClassLoader());

            // 从MainClass作为入口进行MainClass代码校验
            Class<?> mClass = classLoader.loadClass(mainClass);
            ContractType.resolve(mClass);

            // 校验完成后需要释放，否则无法删除该Jar文件
            classLoader.close();

            LOGGER.debug(String.format("Verify Jar [%s] 's MainClass end...", mainClassJarFile.getName()));
        } catch (Exception e) {
            throw new MojoFailureException(e.getMessage());
        }
    }

    public File verify(File defaultJarFile) throws MojoFailureException {
        try {
            // 检查jar包中所有的class的命名，要求其包名不能为com.jd.blockchain.*
            LinkedList<String> totalClasses = loadAllClass(defaultJarFile);

            if (!totalClasses.isEmpty()) {

                for (String clazz : totalClasses) {

                    String dotClassName = dotClassName(clazz);

                    LOGGER.debug(String.format("Verify Dependency Class[%s] start......", dotClassName));
                    // 获取其包名
                    // 将class转换为包名
                    String packageName = class2Package(dotClassName);

                    if (ContractJarUtils.isJDChainPackage(packageName)) {
                        throw new IllegalStateException(String.format("Class[%s]'s package[%s] cannot start with %s !",
                                dotClassName, packageName, ContractJarUtils.JDCHAIN_PACKAGE));
                    }

                    LOGGER.debug(String.format("Verify Class[%s] end......", clazz));
                }
            }

            // 处理完成之后，生成finalName-JDChain-Contract.jar
            return compileCustomJar(defaultJarFile);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new MojoFailureException(e.getMessage());
        }
    }

    private String class2Package(String dotClassName) {

        return dotClassName.substring(0, dotClassName.lastIndexOf("."));
    }

    private File compileCustomJar(File defaultJarFile) throws IOException {

        String fileParentPath = defaultJarFile.getParentFile().getPath();

        String jarFileName = defaultJarFile.getName();

        String fileName = jarFileName.substring(0, jarFileName.lastIndexOf("."));

        // 首先将Jar包转换为指定的格式
        String dstJarPath = fileParentPath + File.separator +
                fileName + "-temp-" + System.currentTimeMillis() + ".jar";

        File srcJar = defaultJarFile, dstJar = new File(dstJarPath);

        LOGGER.debug(String.format("Jar from [%s] to [%s] Copying", defaultJarFile.getPath(), dstJarPath));
        // 首先进行Copy处理
        copy(srcJar, dstJar);

        LOGGER.debug(String.format("Jar from [%s] to [%s] Copied", defaultJarFile.getPath(), dstJarPath));

        byte[] txtBytes = contractMF(FileUtils.readFileToByteArray(dstJar)).getBytes(StandardCharsets.UTF_8);

        String finalJarPath = fileParentPath + File.separator + fileName + "-JDChain-Contract.jar";

        File finalJar = new File(finalJarPath);

        // 生成最终的Jar文件
        copy(dstJar, finalJar, contractMFJarEntry(), txtBytes, null);

        // 删除临时文件
        FileUtils.forceDelete(dstJar);

        return finalJar;
    }

    private ClassLoader verifyMainClass(File jarFile) throws Exception {
        // 加载main-class，开始校验类型
        LOGGER.debug(String.format("Verify Jar [%s] 's MainClass start...", jarFile.getName()));
        URL jarURL = jarFile.toURI().toURL();
        ClassLoader classLoader = new URLClassLoader(new URL[]{jarURL});
        Class<?> mClass = classLoader.loadClass(mainClass);
        ContractType.resolve(mClass);
        LOGGER.debug(String.format("Verify Jar [%s] 's MainClass end...", jarFile.getName()));
        return classLoader;
    }

    private LinkedList<String> loadAllClass(File file) throws Exception {
        JarFile jarFile = new JarFile(file);
        LinkedList<String> allClass = new LinkedList<>();
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.endsWith(".class")) {
                // 内部类，不需要处理
                if (!entryName.contains("$")) {
                    allClass.addLast(entryName.substring(0, entryName.length() - 6));
                }
            }
        }
        // Jar文件使用完成后需要关闭，否则可能会产生无法删除的问题
        jarFile.close();

        return allClass;
    }
}
