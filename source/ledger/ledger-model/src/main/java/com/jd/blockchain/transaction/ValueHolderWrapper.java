package com.jd.blockchain.transaction;

class ValueHolderWrapper {
	private OperationResultHolder valueHolder;

	protected ValueHolderWrapper(OperationResultHolder valueHolder) {
		this.valueHolder = valueHolder;
	}

	public boolean isCompleted() {
		return valueHolder.isCompleted();
	}

	public Throwable getError() {
		return valueHolder.getError();
	}

	/**
	 * 获取值；<br>
	 * 
	 * 此方法不堵塞，调用立即返回；<br>
	 * 
	 * 如果未完成时（ {@link #isCompleted()} 为 false ），总是返回 null；
	 * 
	 * @return
	 */
	protected Object getValue() {
		return valueHolder.getResult();
	}

	public void addCompletedListener(OperationCompletedListener listener) {
		valueHolder.addCompletedListener(listener);
	}
}