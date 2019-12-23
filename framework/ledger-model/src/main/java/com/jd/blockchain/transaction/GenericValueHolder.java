package com.jd.blockchain.transaction;

public class GenericValueHolder<T> extends ValueHolderWrapper {

	GenericValueHolder(OperationResultHolder resultHolder) {
		super(resultHolder);
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
	@SuppressWarnings("unchecked")
	public T get() {
		return (T) super.getValue();
	}

}