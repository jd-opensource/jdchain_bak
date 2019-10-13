package com.jd.blockchain.ump.service;

import com.jd.blockchain.ump.model.UmpConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class SecurityServiceHandler implements SecurityService {

    private static final String PATH_INNER = "/";

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityServiceHandler.class);

    static final String SECURITY_FILE = "security.default.config";

    private List<String> securityConfigs = new ArrayList<>();

    private List<String> participantRoleConfigs = new ArrayList<>();

    public SecurityServiceHandler() {
        init();
    }

    @Override
    public List<String> securityConfigs() {
        return securityConfigs;
    }

    @Override
    public List<String> participantRoleConfigs() {
        return participantRoleConfigs;
    }

    @Override
    public void init() {

        try {
            // 读取配置文件中的内容
            InputStream currentFileInputStream = SecurityServiceHandler.class.getResourceAsStream(
                    PATH_INNER + SECURITY_FILE);

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

            securityConfigs.add(propBuild(UmpConstant.SECURITY_ROLES, roles));

            String[] rolesArray = roles.split(",");
            for (String role : rolesArray) {
                String roleRealm = role.trim();
                String roleLedgerKey = String.format(UmpConstant.SECURITY_ROLES_PRIVILEGES_LEDGER_FORMAT, roleRealm);
                String roleLedgerValue = currentProps.getProperty(roleLedgerKey, "");
                securityConfigs.add(propBuild(roleLedgerKey, roleLedgerValue));

                String roleTxKey = String.format(UmpConstant.SECURITY_ROLES_PRIVILEGES_TX_FORMAT, roleRealm);
                String roleTxValue = currentProps.getProperty(roleTxKey, "");
                securityConfigs.add(propBuild(roleTxKey, roleTxValue));
            }

            // 将参与方信息写入
            String partiRolesValue = currentProps.getProperty(UmpConstant.SECURITY_PARTI_ROLES, "");
            String partiRolesPolicyValue = currentProps.getProperty(UmpConstant.SECURITY_PARTI_ROLES_POLICY, "");

            participantRoleConfigs.add(propBuild(UmpConstant.SECURITY_PARTI_ROLES, partiRolesValue));
            participantRoleConfigs.add(propBuild(UmpConstant.SECURITY_PARTI_ROLES_POLICY, partiRolesPolicyValue));

        } else {
            // 打印日志即可
            LOGGER.error("Can not find Properties from {}", SECURITY_FILE);
        }
    }

    private String propBuild(String key, String value) {
        return key + "=" + value;
    }
}
