package com.jd.blockchain.ump;

import org.springframework.boot.SpringApplication;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class UmpBooter {

    private static final String ARG_PORT = "-p";

    private static final String ARG_HOST = "-h";

    private static final String CONFIG = "BOOT-INF" + File.separator + "classes" + File.separator + "config.properties";

    private static final String CONFIG_PROP_HOST = "server.host";

    private static final String CONFIG_PROP_HOST_DEFAULT = "0.0.0.0";

    private static final String CONFIG_PROP_PORT = "server.port";

    private static final String CONFIG_PROP_PORT_DEFAULT = "8080";

    private static final String CONFIG_PROP_DB_URL = "db.url";

    private static final String CONFIG_PROP_DB_URL_DEFAULT = "rocksdb://#project#/jumpdb";

    public static void main(String[] args) {
        try {
            startServer(server(args));
            System.out.println("Server Start SUCCESS !!!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.printf("Server Start FAIL -> %s, Exit JVM !!!", e.toString());
            // 正常退出
            System.exit(0);
        }
    }

    private static void startServer(Server server) {

        System.out.printf("server.address = %s, server.port = %s, db.url = %s \r\n",
                server.host, server.port, server.dbUrl);

        List<String> argList = new ArrayList<>();
        argList.add(String.format("--server.address=%s", server.host));
        argList.add(String.format("--server.port=%s", server.port));
        argList.add(String.format("--db.url=%s", server.dbUrl));

        String[] args = argList.toArray(new String[argList.size()]);

        // 启动服务器；
        SpringApplication.run(UmpConfiguration.class, args);
    }

    private static Server server(String[] args) {
        Server defaultServer = serverFromConfig();
        if (args == null || args.length == 0) {
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
            InputStream inputStream = UmpBooter.class.getResourceAsStream(File.separator + CONFIG);
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
    }
}
