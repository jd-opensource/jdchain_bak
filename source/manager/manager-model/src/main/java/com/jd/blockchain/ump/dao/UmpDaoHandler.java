package com.jd.blockchain.ump.dao;

import com.jd.blockchain.ump.model.UmpConstant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
public class UmpDaoHandler implements UmpDao, CommandLineRunner, DBConnection {

    public final String PROJECT_FLAG = "#project#";

    private static final String PROTOCOL_FILE = "file:";

    private static final String INNER_FILE_SEPARATOR = "!";

    private static final String PROTOCOL_SEPARATOR = "://";

    private DBConnection dbConnection;

    @Override
    public void run(String... args) {

        String dbUrl = RocksDBConnection.SCHEMA + PROTOCOL_SEPARATOR +
                PROJECT_FLAG + File.separator + UmpConstant.DB_NAME;

        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith("--db.url")) {
                    dbUrl = arg.split("=")[1];
                }
            }
        }

        dbConnection = DBConnectionProvider.dbConnection(realPath(dbUrl));

        initProjectPath();
    }

    private void initProjectPath() {
        UmpConstant.PROJECT_PATH = projectPath();
        System.out.printf("Init Project Path = %s \r\n", UmpConstant.PROJECT_PATH);
    }

    @Override
    public String dbSchema() {
        return null;
    }

    @Override
    public DBConnection initDbUrl(String dbUrl) {
        return dbConnection;
    }

    @Override
    public void put(String key, String value) {
        dbConnection.put(key, value);
    }

    @Override
    public void put(String key, Object value, Class<?> type) {
        dbConnection.put(key, value, type);
    }

    @Override
    public String get(String key) {
        return dbConnection.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return dbConnection.get(key,type);
    }

    @Override
    public void delete(String key) {
        dbConnection.delete(key);
    }

    @Override
    public boolean exist(String dbUrl) {
        try {
            return dbConnection.exist(dbUrl);
        } catch (Exception e) {
            // 不关心异常
            System.err.println(e);
            return false;
        }
    }

    private String realPath(String dbUrl) {
        if (dbUrl.contains(PROJECT_FLAG)) {
            // 获取当前jar包路径
            try {
                String projectPath = projectPath();
                return dbUrl.replaceAll(PROJECT_FLAG, projectPath);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return dbUrl;
    }

    private String projectPath() {
        File jarDirectory = new File(jarRootPath());
        return jarDirectory.getParentFile().getParentFile().getPath();
    }

    private String jarRootPath() {
        // 获取Jar包所在路径
        String jarRootPath = UmpDaoHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        // 处理打包到SpringBoot后路径问题：file:
        if (jarRootPath.startsWith(PROTOCOL_FILE)) {
            jarRootPath = jarRootPath.substring(PROTOCOL_FILE.length());
        }
        // 处理打包到SpringBoot后内部分隔符问题：!
        if (jarRootPath.contains(INNER_FILE_SEPARATOR)) {
            jarRootPath = jarRootPath.substring(0, jarRootPath.indexOf(INNER_FILE_SEPARATOR));
        }

        return jarRootPath;
    }
}
