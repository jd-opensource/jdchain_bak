package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.UmpConstant;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class SecurityServiceHandler implements SecurityService {

    static final String SECURITY_FILE = "security.config";

    private List<String> securityConfigs = new ArrayList<>();

    public SecurityServiceHandler() {
        init();
    }

    @Override
    public List<String> securityConfigs() {
        return securityConfigs;
    }

    @Override
    public void init() {
        try {
            // 读取配置文件中的内容
            InputStream currentFileInputStream = SecurityServiceHandler.class.getResourceAsStream(
                    File.separator + SECURITY_FILE);

            Properties currentProps = new Properties();

            currentProps.load(currentFileInputStream);

            // 将配置文件内容写入securityConfigs
            write(currentProps);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void write(Properties currentProps) {
        // 获取ROLES
        String roles = currentProps.getProperty(UmpConstant.SECURITY_ROLES, "");

        if (roles.length() > 0) {




        }
    }
}
