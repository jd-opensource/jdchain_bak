package com.jd.blockchain.contract;

import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.utils.io.BytesUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class ContractJarUtils {

    public static final String BLACK_CONF = "blacks.conf";

    public static final String WHITE_CONF = "whites.conf";

    private static final String CONTRACT_MF = "META-INF/CONTRACT.MF";

    private static final HashFunction HASH_FUNCTION = Crypto.getHashFunction("SHA256");

    private static final Random FILE_RANDOM = new Random();

    private static final byte[] JDCHAIN_MARK = "JDChain".getBytes(StandardCharsets.UTF_8);

    public static final String JDCHAIN_PACKAGE = "com.jd.blockchain";

    public static boolean isJDChainPackage(String packageName) {
        if (packageName.equals(JDCHAIN_PACKAGE)) {
            return true;
        }
        return packageName.startsWith(JDCHAIN_PACKAGE + ".");
    }

    public static List<String> loadWhiteConf() {

        return resolveConfig(WHITE_CONF);
    }

    public static List<String> loadBlackConf() {
        return resolveConfig(BLACK_CONF);
    }

    public static List<String> resolveConfig(String fileName) {
        List<String> configs = new ArrayList<>();

        try {
            List<String> readLines = loadConfig(fileName);
            if (!readLines.isEmpty()) {
                for (String readLine : readLines) {
                    String[] lines = readLine.split(",");
                    configs.addAll(Arrays.asList(lines));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return configs;
    }

    public static List<String> loadConfig(String fileName) throws Exception {

        return IOUtils.readLines(
                ContractJarUtils.class.getResourceAsStream(File.separator + fileName));
    }

    public static Map<String, byte[]> loadAllClasses(final File jar) throws Exception {
        Map<String, byte[]> allClasses = new HashMap<>();
        JarFile jarFile = new JarFile(jar);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while(jarEntries.hasMoreElements()){
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName();
            if (verify(entryName)) {
                byte[] classContent = readStream(jarFile.getInputStream(jarEntry));
                if (classContent != null && classContent.length > 0) {
                    allClasses.put(entryName, classContent);
                }
            }
        }
        jarFile.close();

        return allClasses;
    }

    private static boolean verify(String entryName) {

        if (entryName.endsWith(".class")
                && !entryName.startsWith("META-INF")
                && !entryName.contains("-")
                && entryName.contains("/")) {
            return true;
        }
        return false;
    }

    public static String dotClassName(String className) {
        String dotClassName = className;
        if (className.endsWith(".class")) {
            dotClassName = className.substring(0, className.length() - 6);
        }
        dotClassName = dotClassName.replaceAll("/", ".");
        return dotClassName;
    }

    public static void verify(String contractPath, byte[] chainCode) {
        if (chainCode == null || chainCode.length == 0) {
            throw new IllegalStateException("Contract's chaincode is empty !!!");
        }
        // 首先生成合约文件
        File jarFile = newJarTempFile();
        try {
            FileUtils.writeByteArrayToFile(jarFile, chainCode);
            // 校验合约文件
            verify(contractPath, jarFile);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            // 删除文件
            try {
                jarFile.deleteOnExit();
            } catch (Exception e) {
                // DO NOTHING
            }
        }
    }

    public static void verify(byte[] chainCode) {
        verify(null, chainCode);
    }

    private static void verify(String contractPath, File jarFile) throws Exception {
        // 首先判断jarFile中是否含有META-INF/JDCHAIN.TXT，并将其读出
        URL jarUrl = new URL("jar:file:" + jarFile.getPath() + "!/" + CONTRACT_MF);
        InputStream inputStream = jarUrl.openStream();
        if (inputStream == null) {
            throw new IllegalStateException(CONTRACT_MF + " IS NULL !!!");
        }
        byte[] bytes;

        try {
            bytes = IOUtils.toByteArray(inputStream);
        } finally {
            inputStream.close();
        }
        if (bytes == null || bytes.length == 0) {
            throw new IllegalStateException(CONTRACT_MF + " IS Illegal !!!");
        }
        // 获取对应的Hash内容
        String txt = new String(bytes, StandardCharsets.UTF_8);

        // 生成新的Jar包文件，该文件路径与JarFile基本一致
        File tempJar = newJarTempFile();
        try {
            // 复制除JDCHAIN.TXT之外的部分
            copy(jarFile, tempJar, null, null, CONTRACT_MF);

            // 生成新Jar包对应的Hash内容
            String verifyTxt = contractMF(FileUtils.readFileToByteArray(tempJar));

            // 校验Jar包内容
            if (!txt.equals(verifyTxt)) {
                throw new IllegalStateException(String.format("Jar [%s] verify Illegal !!!", jarFile.getName()));
            }
        } finally {
            try {
                // 删除临时文件
                tempJar.deleteOnExit();
            } catch (Exception e) {
                // DO NOTHING
            }
        }
    }

    public static void copy(File srcJar, File dstJar) throws IOException {
        copy(srcJar, dstJar, null, null, null);
    }

    public static void copy(File srcJar, File dstJar, JarEntry addEntry, byte[] addBytes, String filter) throws IOException {
        JarFile jarFile = new JarFile(srcJar);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        JarOutputStream jarOut = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(dstJar)));

        while(jarEntries.hasMoreElements()){
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName();
            if (filter != null && filter.equals(entryName)) {
                continue;
            }
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

    public static String contractMF(byte[] content) {
        HashDigest hashDigest = HASH_FUNCTION.hash(BytesUtils.concat(content, JDCHAIN_MARK));
        return "hash:" + hashDigest.toBase58();
    }

    public static JarEntry contractMFJarEntry() {
        return new JarEntry(CONTRACT_MF);
    }

    private static byte[] readStream(InputStream inputStream) {
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

    private static File newJarTempFile() {

        try {
            return File.createTempFile("contract-" +
                    System.currentTimeMillis() + "-" +
                    System.nanoTime() + "-" +
                    FILE_RANDOM.nextInt(1024), ".jar");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
//
//        if (contractPath != null && contractPath.length() > 0) {
//            return new File(contractPath + File.separator +
//                    "contract-" +
//                    System.currentTimeMillis() + "-" +
//                    System.nanoTime() + "-" +
//                    FILE_RANDOM.nextInt(1024) +
//                    ".jar");
//        }
//
//        return new File("contract-" +
//                System.currentTimeMillis() + "-" +
//                System.nanoTime() + "-" +
//                FILE_RANDOM.nextInt(1024) +
//                ".jar");
    }
}
