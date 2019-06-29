package com.jd.blockchain;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.jd.blockchain.contract.ContractType;
import com.jd.blockchain.utils.IllegalDataException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;

/**
 * first step, we want to parse the source code by javaParse. But it's repeated and difficult to parse the source.
 * This is a try of "from Initail to Abandoned".
 * Since we are good at the class, why not?
 * Now we change a way of thinking, first we pre-compile the source code, then parse the *.jar.
 *
 * by zhaogw
 * date 2019-06-05 16:17
 */
@Mojo(name = "JDChain.Verify")
public class ContractVerifyMojo extends AbstractMojo {

    Logger logger = LoggerFactory.getLogger(ContractVerifyMojo.class);

    private static final String JDCHAIN_META = "META-INF/JDCHAIN.TXT";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * jar's name;
     */
    @Parameter
    private String finalName;

    @Override
    public void execute() throws MojoFailureException {

        List<Path> sources;
        try {

            File jarFile = copyAndManage();

            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String[] packageBlackList = properties.getProperty("blacklist").split(",");
            Path baseDirPath = project.getBasedir().toPath();
            sources = Files.find(baseDirPath, Integer.MAX_VALUE, (file, attrs) -> (file.toString().endsWith(".java"))).collect(Collectors.toList());
            for (Path path : sources) {
                CompilationUnit compilationUnit = JavaParser.parse(path);

                compilationUnit.accept(new MethodVisitor(), null);

                NodeList<ImportDeclaration> imports = compilationUnit.getImports();
                for (ImportDeclaration imp : imports) {
                    String importName = imp.getName().asString();
                    for (String item : packageBlackList) {
                        if (importName.startsWith(item)) {
                            throw new MojoFailureException("在源码中不允许包含此引入包:" + importName);
                        }
                    }
                }

                //now we parse the jar;
                URL jarURL = jarFile.toURI().toURL();
                ClassLoader classLoader = new URLClassLoader(new URL[]{jarURL},this.getClass().getClassLoader());
                Attributes m = new JarFile(jarFile).getManifest().getMainAttributes();
                String contractMainClass = m.getValue(Attributes.Name.MAIN_CLASS);
                try {
                    Class mainClass = classLoader.loadClass(contractMainClass);
                    ContractType.resolve(mainClass);
                } catch (ClassNotFoundException e) {
                    throw new IllegalDataException(e.getMessage());
                }
            }
        } catch (IOException exception) {
            logger.error(exception.getMessage());
            throw new MojoFailureException("IO ERROR");
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
    }

    private File copyAndManage() throws IOException {
        // 首先将Jar包转换为指定的格式
        String srcJarPath = project.getBuild().getDirectory() +
                File.separator + finalName + ".jar";

        String dstJarPath = project.getBuild().getDirectory() +
                File.separator + finalName + "-temp-" + System.currentTimeMillis() + ".jar";

        File srcJar = new File(srcJarPath), dstJar = new File(dstJarPath);

        // 首先进行Copy处理
        copy(srcJar, dstJar);

        byte[] txtBytes = jdChainTxt(FileUtils.readFileToByteArray(dstJar)).getBytes(StandardCharsets.UTF_8);

        String finalJarPath = project.getBuild().getDirectory() +
                File.separator + finalName + "-jdchain.jar";

        File finalJar = new File(finalJarPath);

        copy(dstJar, finalJar, new JarEntry(JDCHAIN_META), txtBytes, null);

        // 删除临时文件
        FileUtils.forceDelete(dstJar);

        return finalJar;
        // 删除srcJar

        // 删除finalJar
//        FileUtils.forceDelete(finalJar);
//        // 删除srcJar
//        srcJar.deleteOnExit();
//
//        // 修改名字
//        finalJar.renameTo(srcJar);
    }

    private void copy(File srcJar, File dstJar) throws IOException {
        copy(srcJar, dstJar, null, null, null);
    }

    private void copy(File srcJar, File dstJar, JarEntry addEntry, byte[] addBytes, String filter) throws IOException {
        JarFile jarFile = new JarFile(srcJar);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        JarOutputStream jarOut = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(dstJar)));

        while(jarEntries.hasMoreElements()){
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName();
            if (filter != null && filter.equals(entryName)) {
                continue;
            }
            System.out.println(entryName);
            jarOut.putNextEntry(jarEntry);
            jarOut.write(readStream(jarFile.getInputStream(jarEntry)));
            jarOut.closeEntry();
        }
        if (addEntry != null) {
            jarOut.putNextEntry(addEntry);
            jarOut.write(addBytes);
            jarOut.closeEntry();
        }

        jarOut.flush();
        jarOut.finish();
        jarOut.close();
        jarFile.close();
    }

    private String jdChainTxt(byte[] content) {
        // hash=Hex(hash(content))
        String hashTxt = "hash:" + DigestUtils.sha256Hex(content);
        System.out.println(hashTxt);
        return hashTxt;
    }

    private byte[] readStream(InputStream inputStream) {
        try (ByteArrayOutputStream outSteam = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            inputStream.close();
            return outSteam.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private  class MethodVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            /* here you can access the attributes of the method.
             this method will be called for all methods in this
             CompilationUnit, including inner class methods */
            logger.info("method:"+n.getName());
            super.visit(n, arg);
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            logger.info("class:"+n.getName()+" extends:"+n.getExtendedTypes()+" implements:"+n.getImplementedTypes());

            super.visit(n, arg);
        }

        @Override
        public void visit(PackageDeclaration n, Void arg) {
            logger.info("package:"+n.getName());
            super.visit(n, arg);
        }

    }
}
