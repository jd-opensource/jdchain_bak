package com.jd.blockchain.transaction;

public class BooleanValueHolder extends ValueHolderWrapper {

	BooleanValueHolder(OperationResultHolder resultHolder) {
		super(resultHolder);
	}

	/**
	 * 获取值；<br>
	 * 
	 * 此方法不堵塞，调用立即返回；<br>
	 * 
	 * 如果未完成时（ {@link #isCompleted()} 为 false ），总是返回 false；
	 * 
	 * @return
	 */
	public boolean get() {
		return super.isCompleted() ? (boolean) super.getValue() : false;
	}

}