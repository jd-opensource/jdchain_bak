package com.jd.blockchain.ump.model.state;

public enum ScheduleState {

    /**
     * 加载内容，包括获取各种数据列表
     */
    LOAD,
    LOAD_SUCCESS, // 加载成功
    LOAD_FAIL, // 加载失败

    /**
     * 将获取的数据写入文件
     *
     */
    WRITE,
    WRITE_SUCCESS,  // 写入文件成功
    WRITE_FAIL,  // 写入文件失败

    /**
     * Ledger_INIT：账本初始化过程
     * 主要是调用SHELL
     *
     */
    INIT,
    INIT_SUCCESS,  // 账本初始化成功
    INIT_FAIL,  // 账本初始化失败

    /**
     * 无须启动PEER，等待PEER自动更新账本信息
     */
    NO_STARTUP,

    /**
     * 启动Peer节点
     */
    STARTUP_START,
    STARTUP_OVER,
    STARTUP_SUCCESS,  // Peer节点启动成功
    STARTUP_FAIL,  // Peer节点启动失败
    ;

}
