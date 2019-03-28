//package com.jd.blockchain.sdk.proxy;
//
//import com.jd.blockchain.ledger.*;
//import com.jd.blockchain.ledger.data.BlockchainOperationFactory;
//import com.jd.blockchain.ledger.data.OpBlob;
//import com.jd.blockchain.ledger.data.PrivilegeSettingOperationBuilder;
//import my.utils.io.ByteArray;
//
//public class CodeDeployOperationBuilder implements CodeDeployOperation {
//
//    private TxTemplate txTemp;
//
//    public CodeDeployOperationBuilder(TxTemplate txTemp) {
//        this.txTemp = txTemp;
//    }
//
//    /**
//     * 修改脚本；
//     *
//     * @param id
//     * @param code        合约代码；
//     * @param codeVersion
//     */
//    @Override
//    public void set(BlockchainIdentity id, String code, long codeVersion) {
//        ContractDeployingOperation codeOperation = BlockchainOperationFactory.getInstance().deploy(id, ByteArray.wrap(code.getBytes()));
//
//        txTemp.addOperation((OpBlob) codeOperation.getOperation());
//    }
//
//    /**
//     * 配置特权操作；
//     *
//     * @param accountAddress 账户地址；
//     * @return
//     */
//    @Override
//    public PrivilegeSettingOperationBuilder configPrivilege(String accountAddress) {
//        return null;
//    }
//
//    /**
//     * 执行针对负载类型 {@link AccountStateType}为 {@link AccountStateType#MAP} 的账户操作；
//     *
//     * @param accountAddress 要操作的账户地址；
//     * @return
//     */
//    @Override
//    public MapStateOperationBuilder updateState(String accountAddress) {
//        return null;
//    }
//
//    /**
//     * 执行定义账户的合约脚本的操作；
//     *
//     * @param accountAddress 要操作的账户地址；
//     * @return
//     */
//    @Override
//    public CodeDeployOperation defineScript(String accountAddress) {
//        return null;
//    }
//
//    /**
//     * 执行调用账户的合约脚本的方法的操作；
//     *
//     * @param accountAddress 要操作的账户地址；
//     * @return
//     */
//    @Override
//    public ScriptInvokingOperation executeScript(String accountAddress) {
//        return null;
//    }
//}
