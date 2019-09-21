package com.jd.blockchain.ump;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


public class UmpBooter {

    private static final String ARG_HOME = "-home";

    private static final String ARG_PORT = "-p";

    private static final String ARG_HOST = "-h";

    private static final String PROTOCOL_FILE = "file:";

    private static final String TOOLS_JAR = "tools.jar";

    private static final String PATH_INNER_JARS = "/";

    private static final String CONFIG = "config.properties";

    private static final String CONFIG_APPLICATION = "application.properties";

    private static final String CONFIG_PROP_HOST = "server.host";

    private static final String CONFIG_PROP_HOST_DEFAULT = "0.0.0.0";

    private static final String CONFIG_PROP_PORT = "server.port";

    private static final String CONFIG_PROP_PORT_DEFAULT = "8080";

    private static final String CONFIG_PROP_DB_URL = "db.url";

    private static final String CONFIG_PROP_DB_URL_DEFAULT = "rocksdb://#project#/jumpdb";

    private static final String UMP_START_CLASS = "com.jd.blockchain.ump.UmpApplicationStarter";

    private static String HOME_DIR = null;

    public static void main(String[] args) {
        try {
            // 设置相关参数
            Server server = server(args);
            // 加载libs/manager下的jar包
            loadJars();
            // 启动Server
            startServer(server);
            System.out.println("JDChain Manager Server Start SUCCESS !!!");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * 启动Server
     *
     * @param server
     * @throws Exception
     */
    private static void startServer(Server server) throws Exception {

        List<String> argList = new ArrayList<>();
        argList.add(String.format("--server.address=%s", server.host));
        argList.add(String.format("--server.port=%s", server.port));
        argList.add(String.format("--db.url=%s", server.dbUrl));

        String[] args = argList.toArray(new String[argList.size()]);

        InputStream inputStream = UmpBooter.class.getResourceAsStream(PATH_INNER_JARS + CONFIG_APPLICATION);
        if (inputStream == null) {
            System.err.printf("File [%s]' inputStream is NULL !!! \r\n", CONFIG_APPLICATION);
        }
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            System.err.println(e);
        }

        /**
         * 通过ClassLoader调用如下方法
         * {@link UmpApplicationStarter#start(String[], Properties)}
         *
         */
        Class<?> applicationClass = Thread.currentThread().getContextClassLoader()
                .loadClass(UMP_START_CLASS);

        Method startMethod = applicationClass.getDeclaredMethod("start", String[].class, Properties.class);

        startMethod.invoke(null, args, props);
    }

    /**
     * 根据入参加载Server对象（配置）
     *
     * @param args
     * @return
     * @throws Exception
     */
    private static Server server(String[] args) throws Exception {
        Server defaultServer = serverFromConfig();
        if (args == null || args.length == 0) {

            // 获取当前Class所在路径
            HOME_DIR = UmpBooter.class.getResource("").toURI().getPath();
            return defaultServer;
        }
        String host = null;

        int port = 0;

        // 读取参数列表
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals(ARG_HOST)) {
                host = args[i + 1];
            } else if (arg.equals(ARG_PORT)) {
                port = Integer.parseInt(args[i + 1]);
            } else if (arg.equals(ARG_HOME)) {
                HOME_DIR = args[i + 1];
            }
        }

        // 参数列表中的数据不完整，则剩余部分数据从配置文件中获取
        if (host == null) {
            host = defaultServer.host;
        }
        if (port == 0) {
            port = defaultServer.port;
        }

        return new Server(host, port, defaultServer.dbUrl);
    }

    /**
     * 配置文件中加载Server对象
     *
     * @return
     */
    private static Server serverFromConfig() {
        try {
            InputStream inputStream = UmpBooter.class.getResourceAsStream(PATH_INNER_JARS + CONFIG);
            if (inputStream == null) {
                System.err.printf("File [%s]' inputStream is NULL !!! \r\n", CONFIG);
            }
            Properties props = new Properties();
            props.load(inputStream);
            String host = props.getProperty(CONFIG_PROP_HOST, CONFIG_PROP_HOST_DEFAULT);
            int port = Integer.parseInt(
                    props.getProperty(CONFIG_PROP_PORT, CONFIG_PROP_PORT_DEFAULT));
            String dbUrl = props.getProperty(CONFIG_PROP_DB_URL, CONFIG_PROP_DB_URL_DEFAULT);
            return new Server(host, port, dbUrl);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 自定义ClassLoader加载Jar包
     *
     */
    private static void loadJars() throws Exception {
        // 获取两个路径下所有的正确的Jar包
        URL[] totalJars = totalURLs();

        // 自定义URLClassLoader
        URLClassLoader totalClassLoader = new URLClassLoader(totalJars,
                Thread.currentThread().getContextClassLoader().getParent());

        // 设置当前线程ClassLoader
        Thread.currentThread().setContextClassLoader(totalClassLoader);
    }

    /**
     * 获取指定路径下的所有Jar
     *
     * @return
     */
    public static URL[] totalURLs() throws Exception {
        List<URL> totalURLs = new ArrayList<>();
        totalURLs.addAll(libsPathURLs());
        totalURLs.addAll(managerPathURLs());
        URL toolsJarURL = toolsJarURL();
        if (toolsJarURL != null) {
            totalURLs.add(toolsJarURL);
            System.out.printf("Loaded tools.jar[%s]! \r\n", toolsJarURL);
        }
        URL[] totalURLArray = new URL[totalURLs.size()];
        return totalURLs.toArray(totalURLArray);
    }

    /**
     * 加载JAVA_HOME下的tools.jar文件
     *
     * @return
     */
    public static URL toolsJarURL() throws Exception {
        // tools.jar位于JAVA_HOME/....../lib/tools.jar
        // 首先从classpath下进行加载
        String classPath = System.getProperty("java.class.path");
        String[] paths = classPath.split(":");
        for (String path : paths) {
            if (path.endsWith("/" + TOOLS_JAR)) {
                // 当前路径即为tools.jar所在路径
                return new URL(PROTOCOL_FILE + path);
            }
        }

        // 获取其JAVA_HOME路径
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome != null && javaHome.length() > 0) {
            String toolsJarPath = javaHome + File.separator + "lib" + File.separator + TOOLS_JAR;
            File toolsJar = new File(toolsJarPath);
            if (toolsJar.exists()) {
                return new URL(PROTOCOL_FILE + toolsJarPath);
            }
        }

        return null;
    }

    /**
     * 获取libs目录下的相关Jar
     *     排除JDChain项目中默认的其他booter对应的Jar包
     *
     * @return
     */
    public static List<URL> libsPathURLs() {
        try {
            File libsDir = new File(HOME_DIR + File.separator + "libs");
            File[] jars = libsDir.listFiles(f ->
                    f.getName().endsWith(".jar") &&
                    f.isFile() &&
                    !f.getName().contains("-booter-") &&
                    !f.getName().contains("tools-initializer")
            );
            List<URL> libsPathURLs = new ArrayList<>();
            if (jars != null && jars.length > 0) {
                for (int i = 0; i < jars.length; i++) {
                    URL jarURL = jars[i].toURI().toURL();
                    libsPathURLs.add(jarURL);
                    System.out.printf("Loaded libsPath Jar[%s]! \r\n", jarURL);
                }
            }
            return libsPathURLs;
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 加载manager下的所有Jar
     *
     * @return
     */
    public static List<URL> managerPathURLs() {
        try {
            File managerDir = new File(HOME_DIR + File.separator + "manager");
            File[] jars = managerDir.listFiles(f -> f.getName().endsWith(".jar") && f.isFile());
            List<URL> managerPathURLs = new ArrayList<>();
            if (jars != null && jars.length > 0) {
                for (int i = 0; i < jars.length; i++) {
                    URL jarURL = jars[i].toURI().toURL();
                    managerPathURLs.add(jarURL);
                    System.out.printf("Loaded ManagerPath Jar[%s]! \r\n", jarURL);
                }
            }
            return managerPathURLs;
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static class Server {

        private String host;

        private int port;

        private String dbUrl;

        public Server(String host, int port, String dbUrl) {
            this.host = host;
            this.port = port;
            this.dbUrl = dbUrl;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getDbUrl() {
            return dbUrl;
        }

        public void setDbUrl(String dbUrl) {
            this.dbUrl = dbUrl;
        }
    }
}
