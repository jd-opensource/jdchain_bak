package com.jdchain.samples.contract;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

/**
 * 合约样例，提供通过合约创建用户/数据账户/事件账户，写入KV，发布事件等功能
 */
@Contract
public interface SampleContract {

    // a. 创建角色，并分配权限
    @ContractEvent(name = "createRoleAndPermissions")
    void createRoleAndPermissions(String role, String ledgerPermissionSemicolonStr, String txPermissionSemicolonStr);

    // b. 注册用户
    @ContractEvent(name = "registerUserByPubKey")
    void registerUserByPubKey(String pubkey);

    // c. 修改用户角色
    @ContractEvent(name = "modifyUserRole")
    void modifyUserRole(String address, String role);

    // d. 修改用户状态
    @ContractEvent(name = "modifyUserState")
    void modifyUserState(String userAddress, String state);

    // e. 注册数据账户
    //void registerDataAccount(String seed);

    // f. 修改数据账户角色及mode
    @ContractEvent(name = "modifyDataAccountRoleAndMode")
    void modifyDataAccountRoleAndMode(String dataAccountAddress, String role, String mode);

    // h. 数据账户赋值，更新值
    @ContractEvent(name = "dataAccountAddress")
    void setKV(String dataAccountAddress, String key, String value, String version);

    // i. 注册事件账户
    // String registerEventAccount(String seed)

    // j. 修改事件账户角色及mode
    @ContractEvent(name = "modifyEventAccountRoleAndMode")
    void modifyEventAccountRoleAndMode(String eventAccountAddress, String role, String mode);

    // k. 发布事件
    @ContractEvent(name = "publishEventAccount")
    void publishEventAccount(String eventAccountAddress, String eventName, String value, String sequence);

    // l. 合约中调用合约
    @ContractEvent(name = "invokeContract")
    void invokeContract(String contractAddress, String method, String argDotStr);

    // m. 合约中部署合约
    @ContractEvent(name = "deployContract")
    String deployContract(String pubkey, byte[] carBytes);

    // n. 修改合约角色及mode
    @ContractEvent(name = "modifyContractRoleAndMode")
    void modifyContractRoleAndMode(String contractAddress, String role, String mode);

    // o. 修改合约状态
    @ContractEvent(name = "modifyContractState")
    void modifyContractState(String contractAddress, String state);


    /**
     * 设置KV
     *
     * @param address 数据账户地址
     * @param key     键
     * @param value   值
     * @param version 版本
     */
    @ContractEvent(name = "setKVWithVersion")
    void setKVWithVersion(String address, String key, String value, long version);

    /**
     * 设置KV，基于最新数据版本
     *
     * @param address 数据账户地址
     * @param key     键
     * @param value   值
     */
    @ContractEvent(name = "setKV")
    void setKV(String address, String key, String value);

    /**
     * 注册用户
     *
     * @param seed 种子，不小于32个字符
     */
    @ContractEvent(name = "registerUser")
    String registerUser(String seed);

    /**
     * 注册数据账户
     *
     * @param seed 种子，不小于32个字符
     */
    @ContractEvent(name = "registerDataAccount")
    String registerDataAccount(String seed);

    /**
     * 注册事件账户
     *
     * @param seed 种子，不小于32个字符
     */
    @ContractEvent(name = "registerEventAccount")
    String registerEventAccount(String seed);

    /**
     * 发布事件
     *
     * @param address  事件账户地址
     * @param topic    消息名称
     * @param content  内容
     * @param sequence 当前消息名称下最大序号（初始为-1）
     */
    @ContractEvent(name = "publishEventWithSequence")
    void publishEventWithSequence(String address, String topic, String content, long sequence);

    /**
     * 发布事件，基于最新时间序号
     *
     * @param address 事件账户地址
     * @param topic   消息名称
     * @param content 内容
     */
    @ContractEvent(name = "publishEvent")
    void publishEvent(String address, String topic, String content);
}
