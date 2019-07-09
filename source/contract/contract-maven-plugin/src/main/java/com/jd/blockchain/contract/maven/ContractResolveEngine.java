package com.jd.blockchain.contract.maven;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.jd.blockchain.contract.ContractType;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.jd.blockchain.contract.ContractJarUtils.*;
import static com.jd.blockchain.contract.maven.ContractCompileMojo.JAR_DEPENDENCE;
import static com.jd.blockchain.utils.decompiler.utils.DecompilerUtils.decompileJarFile;

public class ContractResolveEngine {

    private static final String JAVA_SUFFIX = ".java";

    private static final String PATH_DIRECT =
            "src" + File.separator +
            "main" + File.separator +
            "java" + File.separator;

    private static final String CONFIG = "config.properties";

    private static final String BLACK_PACKAGE_LIST = "black.package.list";

    private static final String BLACK_CLASS_LIST = "black.class.list";

    private static final String BLACK_NAME_LIST = "black.name.list";

    private Log LOGGER;

    private MavenProject project;

    private String finalName;

    public ContractResolveEngine(Log LOGGER, MavenProject project, String finalName) {
        this.LOGGER = LOGGER;
        this.project = project;
        this.finalName = finalName;
    }

    public void compileAndVerify() throws MojoFailureException {
        try {
            jarCopy();
            verify(compileCustomJar());
        } catch (IOException e) {
            throw new MojoFailureException("IO Error : " + e.getMessage(), e);
        } catch (MojoFailureException ex) {
            throw ex;
        }
    }

    private void jarCopy() throws IOException {
        String srcJarPath = project.getBuild().getDirectory() +
                File.separator + finalName + "-" + JAR_DEPENDENCE + ".jar";
        String dstJarPath = project.getBuild().getDirectory() +
                File.separator + finalName + ".jar";
        FileUtils.copyFile(new File(srcJarPath), new File(dstJarPath));
    }

    private File compileCustomJar() throws IOException {
        return copyAndManage(project, finalName);
    }

    private void verify(File jarFile) throws MojoFailureException {
        try {
            // 首先校验MainClass
            try {
                verifyMainClass(jarFile);
            } catch (Exception e) {
                jarFile.delete();
                LOGGER.error(e.getMessage());
                throw e;
            }

            Properties config = loadConfig();

            List<ContractPackage> blackNameList = blackNameList(config);

            List<ContractPackage> blackPackageList = blackPackageList(config);

            Set<String> blackClassSet = blackClassSet(config);

            LinkedList<String> totalClassList = loadAllClass(jarFile);
            // 该项目路径
            String projectDir = project.getBasedir().getPath();
            // 代码路径
            String codeBaseDir = projectDir + File.separator + PATH_DIRECT;

            if (!totalClassList.isEmpty()) {

                boolean isOK = true;

                for (String clazz : totalClassList) {

                    LOGGER.debug(String.format("Verify Class[%s] start......", clazz));
                    // 获取其包名
                    String packageName = packageName(clazz);

                    LOGGER.debug(String.format("Class[%s] 's package name = %s", clazz, packageName));

                    // 包的名字黑名单，不能打包该类进入Jar包中，或者合约不能命名这样的名字
                    boolean isNameBlack = false;
                    for (ContractPackage blackName : blackNameList) {
                        isNameBlack = verifyPackage(packageName, blackName);
                        if (isNameBlack) {
                            break;
                        }
                    }

                    // 假设是黑名单则打印日志
                    if (isNameBlack) {
                        // 打印信息供检查
                        LOGGER.error(String.format("Class[%s]'s Package-Name belong to BlackNameList !!!", clazz));
                        isOK = false;
                        continue;
                    }

                    // 获取该Class对应的Java文件
                    File javaFile = new File(codeBaseDir + clazz + JAVA_SUFFIX);

                    boolean isNeedDelete = false;
                    if (!javaFile.exists()) {
                        LOGGER.debug(String.format("Class[%s] -> Java[%s] is not exist, start decompile ...", clazz, jarFile.getPath()));
                        // 表明不是项目中的内容，需要通过反编译获取该文件
                        String source = null;
                        try {
                            source = decompileJarFile(jarFile.getPath(), clazz, true, StandardCharsets.UTF_8.name());
                            if (source == null || source.length() == 0) {
                                throw new IllegalStateException();
                            }
                        } catch (Exception e) {
                            LOGGER.warn(String.format("Decompile Jar[%s]->Class[%s] Fail !!!", jarFile.getPath(), clazz));
                        }
                        // 将source写入Java文件
                        File sourceTempJavaFile = new File(tempPath(codeBaseDir, clazz));
                        FileUtils.writeStringToFile(sourceTempJavaFile, source == null ? "" : source);
                        javaFile = sourceTempJavaFile;
                        isNeedDelete = true;
                    } else {
                        LOGGER.debug(String.format("Class[%s] -> Java[%s] is exist", clazz, jarFile.getPath()));
                    }

                    LOGGER.info(String.format("Parse Java File [%s] start......", javaFile.getPath()));
                    // 解析文件中的内容
                    CompilationUnit compilationUnit = JavaParser.parse(javaFile);

                    MethodVisitor methodVisitor = new MethodVisitor();

                    compilationUnit.accept(methodVisitor, null);

                    List<String> imports = methodVisitor.importClasses;

                    if (!imports.isEmpty()) {
                        for (String importClass : imports) {
                            LOGGER.debug(String.format("Class[%s] read import -> [%s]", clazz, importClass));
                            if (importClass.endsWith("*")) {
                                // 导入的是包
                                for (ContractPackage blackPackage : blackPackageList) {
                                    String importPackageName = importClass.substring(0, importClass.length() - 2);
                                    if (verifyPackage(importPackageName, blackPackage)) {
                                        // 打印信息供检查
                                        LOGGER.error(String.format("Class[%s]'s import class [%s] belong to BlackPackageList !!!", clazz, importClass));
                                        isOK = false;
                                        break;
                                    }
                                }
                            } else {
                                // 导入的是具体的类，则判断类黑名单 + 包黑名单
                                if (blackClassSet.contains(importClass)) {
                                    // 包含导入类，该方式无法通过验证
                                    LOGGER.error(String.format("Class[%s]'s import class [%s] belong to BlackClassList !!!", clazz, importClass));
                                    isOK = false;
                                } else {
                                    // 判断导入的该类与黑名单导入包的对应关系
                                    for (ContractPackage blackPackage : blackPackageList) {
                                        if (verifyClass(importClass, blackPackage)) {
                                            LOGGER.error(String.format("Class[%s]'s import class [%s] belong to BlackPackageList !!!", clazz, importClass));
                                            isOK = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (isNeedDelete) {
                        javaFile.delete();
                    }
                    LOGGER.debug(String.format("Verify Class[%s] end......", clazz));
                }
                if (!isOK) {
                    // 需要将该Jar删除
                    jarFile.delete();
                    throw new IllegalStateException("There are many Illegal information, please check !!!");
                }
            } else {
                jarFile.delete();
                throw new IllegalStateException("There is none class !!!");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void verifyMainClass(File jarFile) throws Exception {
        // 加载main-class，开始校验类型
        LOGGER.debug(String.format("Verify Jar [%s] 's MainClass start...", jarFile.getName()));
        URL jarURL = jarFile.toURI().toURL();
        ClassLoader classLoader = new URLClassLoader(new URL[]{jarURL}, this.getClass().getClassLoader());
        Attributes m = new JarFile(jarFile).getManifest().getMainAttributes();
        String contractMainClass = m.getValue(Attributes.Name.MAIN_CLASS);
        Class mainClass = classLoader.loadClass(contractMainClass);
        ContractType.resolve(mainClass);
        LOGGER.debug(String.format("Verify Jar [%s] 's MainClass end...", jarFile.getName()));
    }

    private List<ContractPackage> blackNameList(Properties config) {
        return blackList(config, BLACK_NAME_LIST);
    }

    private Set<String> blackClassSet(Properties config) {
        Set<String> blackClassSet = new HashSet<>();
        String attrProp = config.getProperty(BLACK_CLASS_LIST);
        if (attrProp != null && attrProp.length() > 0) {
            String[] attrPropArray = attrProp.split(",");
            for (String attr : attrPropArray) {
                LOGGER.info(String.format("Config [%s] -> [%s]", BLACK_CLASS_LIST, attr));
                blackClassSet.add(attr.trim());
            }
        }
        return blackClassSet;
    }

    private List<ContractPackage> blackPackageList(Properties config) {
        return blackList(config, BLACK_PACKAGE_LIST);
    }

    private List<ContractPackage> blackList(Properties config, String attrName) {
        List<ContractPackage> list = new ArrayList<>();
        String attrProp = config.getProperty(attrName);
        if (attrProp != null || attrProp.length() > 0) {
            String[] attrPropArray = attrProp.split(",");
            for (String attr : attrPropArray) {
                LOGGER.info(String.format("Config [%s] -> [%s]", attrName, attr));
                list.add(new ContractPackage(attr));
            }
        }
        return list;
    }

    private boolean verifyPackage(String packageName, ContractPackage contractPackage) {
        boolean verify = false;
        if (packageName.equals(contractPackage.packageName)) {
            // 完全相同
            verify = true;
        } else if (packageName.startsWith(contractPackage.packageName) &&
                contractPackage.isTotal) {
            // 以某个包开头
            verify = true;
        }
        return verify;
    }

    private boolean verifyClass(String className, ContractPackage contractPackage) {
        boolean verify = false;

        if (contractPackage.isTotal) {
            // 表示该包下面的其他所有包都会受限制，此处需要判断起始
            if (className.startsWith(contractPackage.packageName)) {
                verify = true;
            }
        } else {
            // 表示该包必须完整匹配ClassName所在包
            // 获取ClassName所在包
            String packageName = packageNameByDot(className);
            if (packageName.equals(contractPackage.packageName)) {
                verify = true;
            }
        }
        return verify;
    }

    private String packageNameByDot(String className) {
        String[] array = className.split(".");
        if (Character.isLowerCase(array[array.length - 2].charAt(0))) {
            // 如果是小写，表示非内部类
            // 获取完整包名
            return className.substring(0, className.lastIndexOf("."));
        }
        // 表示为内部类，该包拼装组成
        StringBuilder buffer = new StringBuilder();
        for (String s : array) {
            if (buffer.length() > 0) {
                buffer.append(".");
            }
            if (Character.isUpperCase(s.charAt(0))) {
                // 表明已经到具体类
                break;
            }
            buffer.append(s);
        }

        if (buffer.length() == 0) {
            throw new IllegalStateException(String.format("Import Class [%s] Illegal !!!", className));
        }

        return buffer.toString();
    }

    private String packageName(String clazz) {
        int index = clazz.lastIndexOf("/");
        String packageName = clazz.substring(0, index);
        return packageName.replaceAll("/", ".");
    }

    private File copyAndManage(MavenProject project, String finalName) throws IOException {
        // 首先将Jar包转换为指定的格式
        String srcJarPath = project.getBuild().getDirectory() +
                File.separator + finalName + ".jar";

        String dstJarPath = project.getBuild().getDirectory() +
                File.separator + finalName + "-temp-" + System.currentTimeMillis() + ".jar";

        File srcJar = new File(srcJarPath), dstJar = new File(dstJarPath);

        LOGGER.debug(String.format("Jar from [%s] to [%s] Copying", srcJarPath, dstJarPath));
        // 首先进行Copy处理
        copy(srcJar, dstJar);
        LOGGER.debug(String.format("Jar from [%s] to [%s] Copied", srcJarPath, dstJarPath));

        byte[] txtBytes = contractMF(FileUtils.readFileToByteArray(dstJar)).getBytes(StandardCharsets.UTF_8);

        String finalJarPath = project.getBuild().getDirectory() +
                File.separator + finalName + "-jdchain.jar";

        File finalJar = new File(finalJarPath);

        copy(dstJar, finalJar, contractMFJarEntry(), txtBytes, null);

        // 删除临时文件
        FileUtils.forceDelete(dstJar);

        return finalJar;
    }

    private Properties loadConfig() throws Exception {

        Properties properties = new Properties();

        properties.load(this.getClass().getClassLoader().getResourceAsStream(CONFIG));

        return properties;
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

    private String tempPath(String codeBaseDir, String clazz) {
        // 获取最后的名称
        String[] classArray = clazz.split("/");
        String tempPath = codeBaseDir + classArray[classArray.length - 1] + "_" +
                System.currentTimeMillis() + "_" + System.nanoTime() + JAVA_SUFFIX;
        return tempPath;
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Void> {

        private List<String> importClasses = new ArrayList<>();

        @Override
        public void visit(ImportDeclaration n, Void arg) {
            importClasses.add(parseClass(n.toString()));
            super.visit(n, arg);
        }

        private String parseClass(String importInfo) {
            String className = importInfo.substring(7, importInfo.length() - 2);
            if (importInfo.startsWith("import static ")) {
                // 获取静态方法的类信息
                className = importInfo.substring(14, importInfo.lastIndexOf("."));
            }
            if (!className.contains(".")) {
                throw new IllegalStateException(String.format("Import Class [%s] is Illegal !!", className));
            }
            return className;
        }
    }

    private static class ContractPackage {

        private String packageName;

        private boolean isTotal = false;

        public ContractPackage() {
        }

        public ContractPackage(String totalPackage) {
            if (totalPackage.endsWith("*")) {
                this.packageName = totalPackage.substring(0, totalPackage.length() - 2).trim();
                this.isTotal = true;
            } else {
                this.packageName = totalPackage;
            }
        }

        public String getPackageName() {
            return packageName;
        }

        public boolean isTotal() {
            return isTotal;
        }
    }
}
