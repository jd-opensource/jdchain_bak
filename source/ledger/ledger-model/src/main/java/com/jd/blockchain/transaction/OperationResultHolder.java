package com.jd.blockchain.transaction;

import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.utils.event.EventMulticaster;

abstract class OperationResultHolder implements OperationResultHandle {

	private Object value;

	private Throwable error;

	private volatile boolean completed;

	private EventMulticaster<OperationCompletedListener> listenerMulticaster =
			new EventMulticaster<>(OperationCompletedListener.class);

	/**
	 * 导致结束的错误；
	 * 
	 * @return
	 */
	public Throwable getError() {
		return error;
	}

	/**
	 * 是否已经处理完成；
	 * 
	 * @return
	 */
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * 获取操作的返回值； <br>
	 * 在操作未完成之前，总是返回 null；<br>
	 * 可以通过 {@link #isCompleted()} 方法判断操作是否已经完成；<br>
	 * 可以通过 {@link #addCompletedListener(OperationCompletedListener)}
	 * 方法添加监听器来监听操作完成的事件；
	 * 
	 * @return
	 */
	public Object getResult() {
		return value;
	}

	/**
	 * 添加操作完成监听器；
	 * 
	 * @param listener
	 */
	public void addCompletedListener(OperationCompletedListener listener) {
		listenerMulticaster.addListener(listener);
	}

	protected abstract Object decodeResult(BytesValue bytesValue);

	@Override
	public Object complete(BytesValue bytesValue) {
		if (this.completed) {
			throw new IllegalStateException(
					"Contract invocation has been completed, and is not allowed to be completed again!");
		}
		this.completed = true;
		this.value = decodeResult(bytesValue);
		OperationCompletedContext context = new OperationCompletedContext(getOperationIndex(), null);
		listenerMulticaster.getBroadcaster().onCompleted(value, null, context);
		return null;
	}

	@Override
	public void complete(Throwable error) {
		if (completed) {
			return;
		}
		this.completed = true;
		this.error = error;
		OperationCompletedContext context = new OperationCompletedContext(getOperationIndex(), null);
		listenerMulticaster.getBroadcaster().onCompleted(null, error, context);
	}
}