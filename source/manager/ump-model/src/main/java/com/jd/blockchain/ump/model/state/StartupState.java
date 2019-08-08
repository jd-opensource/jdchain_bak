package com.jd.blockchain.ump.model.state;

public enum StartupState {

    /**
     * UNEXIST
     *     不存在，描述该账本Hash曾经创建，但目前在LedgerBinding.conf文件中不存在
     *     此状态不支持任何其他操作
     */
    UNEXIST,

    /**
     * UNLOAD
     *     账本存在，但未加载
     *     此状态可以启动，不能停止
     */
    UNLOAD,

    /**
     * LOADING
     *     账本加载中，说明程序已经启动，但尚未加载该程序
     *     此状态不可以启动，不建议停止
     */
    LOADING,

    /**
     * LOADED
     *     账本已加载
     *     此状态不可以启动，后续可以支持停止操作
     */
    LOADED,

    /**
     * UNKNOWN
     *     未知，常见于命令检测执行错误或程序启动，但账本尚未加载完成
     *     此状态不支持任何其他操作
     */
    UNKNOWN,

    /**
     * DB_UNEXIST
     *     该账本对应的数据库不存在
     *     此状态不支持任何其他操作
     */
    DB_UNEXIST,

    ;
}
