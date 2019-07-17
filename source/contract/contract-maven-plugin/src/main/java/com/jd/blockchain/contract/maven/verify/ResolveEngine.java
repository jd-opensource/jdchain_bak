package com.jd.blockchain.contract.maven.verify;

import com.jd.blockchain.contract.ContractJarUtils;
import com.jd.blockchain.contract.ContractType;
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

    private File jarFile;

    private String mainClass;

    public ResolveEngine(Log LOGGER, File jarFile, String mainClass) {
        this.LOGGER = LOGGER;
        this.jarFile = jarFile;
        this.mainClass = mainClass;
    }

    public File verify() throws MojoFailureException {
        try {
            // 首先校验MainClass
            ClassLoader classLoader = verifyMainClass(jarFile);

            // 检查jar包中所有的class的命名，要求其包名不能为com.jd.blockchain.*
            LinkedList<String> totalClasses = loadAllClass(jarFile);

            if (!totalClasses.isEmpty()) {

                for (String clazz : totalClasses) {

                    String dotClassName = dotClassName(clazz);

                    LOGGER.debug(String.format("Verify Dependency Class[%s] start......", dotClassName));
                    // 获取其包名
                    Class<?> currentClass = classLoader.loadClass(dotClassName);

                    String packageName = currentClass.getPackage().getName();

                    if (ContractJarUtils.isJDChainPackage(packageName)) {
                        throw new IllegalStateException(String.format("Class[%s]'s package[%s] cannot start with %s !",
                                dotClassName, packageName, ContractJarUtils.JDCHAIN_PACKAGE));
                    }

                    LOGGER.debug(String.format("Verify Class[%s] end......", clazz));
                }
            }

            // 处理完成之后，生成finalName-JDChain-Contract.jar
            return compileCustomJar();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new MojoFailureException(e.getMessage());
        }
    }

    private File compileCustomJar() throws IOException {

        String fileParentPath = jarFile.getParentFile().getPath();

        String jarFileName = jarFile.getName();

        String fileName = jarFileName.substring(0, jarFileName.lastIndexOf("."));

        // 首先将Jar包转换为指定的格式
        String dstJarPath = fileParentPath + File.separator +
                fileName + "-temp-" + System.currentTimeMillis() + ".jar";

        File srcJar = jarFile, dstJar = new File(dstJarPath);

        LOGGER.debug(String.format("Jar from [%s] to [%s] Copying", jarFile.getPath(), dstJarPath));
        // 首先进行Copy处理
        copy(srcJar, dstJar);

        LOGGER.debug(String.format("Jar from [%s] to [%s] Copied", jarFile.getPath(), dstJarPath));

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
        return allClass;
    }
}
