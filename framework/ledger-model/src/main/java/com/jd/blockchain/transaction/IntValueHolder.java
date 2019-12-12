package com.jd.blockchain.transaction;

public class IntValueHolder extends ValueHolderWrapper {

	IntValueHolder(OperationResultHolder resultHolder) {
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
	public int get() {
		return super.isCompleted() ? (int) super.getValue() : 0;
	}

}