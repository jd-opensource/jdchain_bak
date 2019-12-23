package com.jd.blockchain.consensus;

public class ConsensusSecurityException extends Exception{

	private static final long serialVersionUID = -164820276123627155L;
	
	public ConsensusSecurityException() {
	}
	
	public ConsensusSecurityException(String message) {
		super(message);
	}
	
	public ConsensusSecurityException(String message, Throwable cause) {
		super(message, cause);
	}

}
