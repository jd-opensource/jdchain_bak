package com.jd.blockchain.transaction;

public class LongValueHolder extends ValueHolderWrapper {

	LongValueHolder(OperationResultHolder resultHolder) {
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
	public long get() {
		return super.isCompleted() ? (long) super.getValue() : 0;
	}

}