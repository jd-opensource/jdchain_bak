package com.jd.blockchain.transaction;

public class ByteValueHolder extends ValueHolderWrapper {

	ByteValueHolder(OperationResultHolder resultHolder) {
		super(resultHolder);
	}

	/**
	 * 获取值；<br>
	 * 
	 * 此方法不堵塞，调用立即返回；<br>
	 * 
	 * 如果未完成时（ {@link #isCompleted()} 为 false ），总是返回 0；
	 * 
	 * @return
	 */
	public byte get() {
		return super.isCompleted() ? (byte) super.getValue() : 0;
	}

}