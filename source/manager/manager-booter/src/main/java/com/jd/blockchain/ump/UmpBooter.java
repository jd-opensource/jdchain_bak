package com.jd.blockchain.ump;

import com.jd.blockchain.ump.web.RetrievalConfigListener;
import com.jd.blockchain.ump.web.UmpConfiguration;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class UmpBooter {

    private static final String ARG_HOME = "-home";

    private static final String ARG_PORT = "-p";

    private static final String ARG_HOST = "-h";

    private static final String CONFIG = "/config.properties";

    private static final String CONFIG_APPLICATION = "BOOT-INF" + File.separator + "classes" + File.separator + "application.properties";

    private static final String CONFIG_PROP_HOST = "server.host";

    private static final String CONFIG_PROP_HOST_DEFAULT = "0.0.0.0";

    private static final String CONFIG_PROP_PORT = "server.port";

    private static final String CONFIG_PROP_PORT_DEFAULT = "8080";

    private static final String CONFIG_PROP_DB_URL = "db.url";

    private static final String CONFIG_PROP_DB_URL_DEFAULT = "rocksdb://#project#/jumpdb";

    private static String HOME_DIR = null;

    public static void main(String[] args) {
        try {
            // 设置相关参数
            Server server = server(args);
            // 加载libs/manager下的jar包
            loadJars();
            // 启动Server
            startServer(server);
            System.out.println("Unified Management Platform Server Start SUCCESS !!!");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static void startServer(Server server) {

        List<String> argList = new ArrayList<>();
        argList.add(String.format("--server.address=%s", server.host));
        argList.add(String.format("--server.port=%s", server.port));
        argList.add(String.format("--db.url=%s", server.dbUrl));

        String[] args = argList.toArray(new String[argList.size()]);

        // 启动服务器；
//        SpringApplication.run(UmpConfiguration.class, args);
        InputStream inputStream = UmpBooter.class.getResourceAsStream(File.separator + CONFIG_APPLICATION);
        if (inputStream == null) {
            System.err.println("InputStream is NULL !!!");
        }
        Properties props = new Properties();
        try {
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 启动服务器；
        SpringApplication springApplication = new SpringApplication(UmpConfiguration.class);
        springApplication.addListeners(new RetrievalConfigListener(props));
        springApplication.run(args);
    }

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

    private static Server serverFromConfig() {
        try {
            InputStream inputStream = UmpBooter.class.getResourceAsStream(CONFIG);
            if (inputStream == null) {
                System.err.println("InputStream is NULL !!!");
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

    private static void loadJars() {
        // 获取两个路径下所有的正确的Jar包
        URL[] libsJars = totalURLs();

        URLClassLoader libClassLoader = new URLClassLoader(libsJars, UmpBooter.class.getClassLoader());

        Thread.currentThread().setContextClassLoader(libClassLoader);
    }

    public static URL[] totalURLs() {
        List<URL> totalURLs = new ArrayList<>();
        totalURLs.addAll(libsPathURLs());
        totalURLs.addAll(managerPathURLs());
        URL[] totalURLArray = new URL[totalURLs.size()];
        return totalURLs.toArray(totalURLArray);
    }

    public static List<URL> libsPathURLs() {
        try {
            File libsDir = new File(HOME_DIR + File.separator + "libs");
            File[] jars = libsDir.listFiles(f -> f.getName().endsWith(".jar") && f.isFile() && !f.getName().contains("-booter-"));
            List<URL> libsPathURLs = new ArrayList<>();
            if (jars != null && jars.length > 0) {
                for (int i = 0; i < jars.length; i++) {
                    libsPathURLs.add(jars[i].toURI().toURL());
                }
            }
            return libsPathURLs;
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static List<URL> managerPathURLs() {
        try {
            File managerDir = new File(HOME_DIR + File.separator + "manager");
            File[] jars = managerDir.listFiles(f -> f.getName().endsWith(".jar") && f.isFile());
            List<URL> managerPathURLs = new ArrayList<>();
            if (jars != null && jars.length > 0) {
                for (int i = 0; i < jars.length; i++) {
                    managerPathURLs.add(jars[i].toURI().toURL());
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
