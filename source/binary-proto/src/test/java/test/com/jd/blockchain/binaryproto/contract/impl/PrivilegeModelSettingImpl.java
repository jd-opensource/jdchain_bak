package test.com.jd.blockchain.binaryproto.contract.impl;

import test.com.jd.blockchain.binaryproto.contract.PrivilegeModelSetting;

/**
 * Created by zhangshuang3 on 2018/7/30.
 */
public class PrivilegeModelSettingImpl implements PrivilegeModelSetting {
    long latestVersion;

    @Override
    public long getLatestVersion() {
        return this.latestVersion;
    }

    public void setLatestVersion(long latestVersion) {
        this.latestVersion = latestVersion;
    }

    //Privilege getPrivilege(long version) {
    //}
}
