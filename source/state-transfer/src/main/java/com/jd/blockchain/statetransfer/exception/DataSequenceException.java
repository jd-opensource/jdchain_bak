package com.jd.blockchain.statetransfer.exception;

/**
 * 数据序列异常处理
 * @author zhangshuang
 * @create 2019/4/18
 * @since 1.0.0
 */
public class DataSequenceException extends RuntimeException {

    private static final long serialVersionUID = -4090881296855827889L;


    public DataSequenceException(String message) {
        super(message);
    }
    public DataSequenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
