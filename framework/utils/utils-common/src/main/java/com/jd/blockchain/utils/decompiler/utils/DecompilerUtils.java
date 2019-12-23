package com.jd.blockchain.utils.decompiler.utils;

import com.jd.blockchain.utils.decompiler.loads.BytesTypeLoader;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.decompiler.Decompiler;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.PlainTextOutput;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarFile;

public class DecompilerUtils {

    public static final AtomicLong SAVE_INDEX = new AtomicLong();

    public static final String MANIFEST_MF = "/META-INF/MANIFEST.MF";

    public static final String MAIN_CLASS = "Main-Class";

    public static String SAVE_DIR = null;

    static {
        init();
    }

    private static void init() {
        try {
            URL url = DecompilerUtils.class
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation();
            String currPath = java.net.URLDecoder.decode(url.getPath(), "UTF-8");
            if (currPath.contains("!/")) {
                currPath = currPath.substring(5, currPath.indexOf("!/"));
            }
            if (currPath.endsWith(".jar")) {
                currPath = currPath.substring(0, currPath.lastIndexOf("/") + 1);
            }
            File file = new File(currPath);
            String homeDir = file.getParent();
            SAVE_DIR = homeDir + File.separator + "temp";
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) {
                dir.mkdir();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    public static String decompile(String classPath) {

        String decompileJava;

        try (StringWriter stringWriter = new StringWriter()) {

            final DecompilerSettings settings = DecompilerSettings.javaDefaults();

            Decompiler.decompile(
                    classPath,
                    new PlainTextOutput(stringWriter),
                    settings
            );
            decompileJava = stringWriter.toString();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
        return decompileJava;
    }

    public static List<String> readManifest2Array(final String jarFilePath, final String charSet) {
        String manifest = readManifest(jarFilePath, charSet);
        String[] manifests = manifest.split("\r\n");
        return Arrays.asList(manifests);
    }

    public static String readManifest(final String jarFilePath, final String charSet) {
        return decompileJarFile(jarFilePath, MANIFEST_MF, false, charSet);
    }

    public static String decompileMainClassFromBytes(byte[] bytes) {
        try {
            String jarFile = writeBytes(bytes, SAVE_DIR, "jar");
            String decompileJava = decompileMainClassFromJarFile(jarFile);
            // 然后删除jarFile文件
            FileUtils.forceDelete(new File(jarFile));
            return decompileJava;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String decompileMainClassFromJarFile(final String jarFilePath) {
        // 首先获取Main-Class
        List<String> manifests = readManifest2Array(jarFilePath, null);
        if (manifests == null || manifests.size() == 0) {
            throw new IllegalStateException("MANIFEST.MF not Exist or is Empty !!!");
        } else {
            String mainClass = null;
            for (String s : manifests) {
                String inner = s.trim().replaceAll(" ", "");
                if (inner.startsWith(MAIN_CLASS)) {
                    mainClass = inner.split(":")[1];
                    break;
                }
            }
            if (mainClass == null || mainClass.length() == 0) {
                throw new IllegalStateException("MANIFEST.MF has not Main-Class !!!");
            }

            // 然后读取MainClass中的内容并进行反编译
            String classPath = mainClass.replaceAll("\\.", "/");
            return decompileJarFile(jarFilePath, classPath, true, null);
        }
    }

    public static String decompileJarFile(final String jarFilePath, final String source, final boolean isClass, final String charSet) {

        // 对于Class文件和非Class文件处理方式不同
        if (!isClass) {
            // 非Class文件不需要编译，直接从文件中读取即可
            String innerSource = source;
            if (!innerSource.startsWith("/")) {
                innerSource = "/" + innerSource;
            }
            try {
                URL jarUrl = new URL("jar:file:" + jarFilePath + "!" + innerSource);
                InputStream inputStream = jarUrl.openStream();
                byte[] bytes = IOUtils.toByteArray(inputStream);
                if (charSet == null) {
                    return new String(bytes);
                }
                return new String(bytes, charSet);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        } else {

            String decompileJava;

            try (StringWriter stringWriter = new StringWriter()) {

                JarFile jarFile = new JarFile(jarFilePath);

                JarTypeLoader jarTypeLoader = new JarTypeLoader(jarFile);

                final DecompilerSettings settings = DecompilerSettings.javaDefaults();

                settings.setTypeLoader(jarTypeLoader);

                Decompiler.decompile(
                        source,
                        new PlainTextOutput(stringWriter),
                        settings
                );
                decompileJava = stringWriter.toString();
            } catch (final Exception e) {
                throw new IllegalStateException(e);
            }
            return decompileJava;
        }
    }

    public static String decompile(byte[] classBytes) {

        String decompileJava;

        try (StringWriter stringWriter = new StringWriter()) {

            BytesTypeLoader bytesTypeLoader = new BytesTypeLoader(classBytes);

            String name = bytesTypeLoader.getName();

            final DecompilerSettings settings = DecompilerSettings.javaDefaults();

            settings.setTypeLoader(bytesTypeLoader);

            Decompiler.decompile(
                    name,
                    new PlainTextOutput(stringWriter),
                    settings
            );
            decompileJava = stringWriter.toString();
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
        return decompileJava;
    }

    public static String decompile(InputStream in) {
        try {
            return decompile(IOUtils.toByteArray(in));
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static byte[] read2Bytes(String filePath) throws IOException {
        return FileUtils.readFileToByteArray(new File(filePath));
    }

    public static String writeBytes(byte[] bytes, String directory, String suffix) throws IOException {
        String saveFileName = System.currentTimeMillis() + "-" + SAVE_INDEX.incrementAndGet() + "." + suffix;
        File saveFile = new File(directory + File.separator + saveFileName);
        FileUtils.writeByteArrayToFile(saveFile, bytes);
        return saveFile.getPath();
    }
}
