package com.jd.blockchain.statetransfer.exception;

public class DataSequenceException extends RuntimeException {

    private static final long serialVersionUID = -4090881296855827889L;


    public DataSequenceException(String message) {
        super(message);
    }
    public DataSequenceException(String message, Throwable cause) {
        super(message, cause);
    }

}
