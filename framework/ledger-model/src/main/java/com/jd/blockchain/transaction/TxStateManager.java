package com.jd.blockchain.transaction;

class TxStateManager {

	private State state = State.OPERABLE;

	public void operate() {
		if (state != State.OPERABLE) {
			throw new IllegalStateException(String.format("Cannot define operations in %s state!", state));
		}
	}

	public void prepare() {
		if (state != State.OPERABLE) {
			throw new IllegalStateException(
					String.format("Cannot switch to %s state in %s state!", State.PREPARED, state));
		}
		state = State.PREPARED;
	}

	public void commit() {
		if (state != State.PREPARED) {
			throw new IllegalStateException(
					String.format("Cannot switch to %s state in %s state!", State.COMMITTED, state));
		}
		state = State.COMMITTED;
	}

	public void complete() {
		if (state != State.COMMITTED) {
			throw new IllegalStateException(String.format("Cannot complete normally in %s state!", state));
		}
		state = State.CLOSED;
	}

	/**
	 * 关闭交易；
	 * 
	 * @param error
	 * @return 此次操作前是否已经处于关闭状态； <br>
	 *         如果返回 true ，则表示之前已经处于关闭状态，此次操作将被忽略；<br>
	 *         如果返回 fasle，则表示之前处于非关闭状态，此次操作将切换为关闭状态；
	 */
	public boolean close() {
		if (state == State.CLOSED) {
			return true;
		}
		state = State.CLOSED;
		return false;
	}

	private static enum State {

		/**
		 * 可操作；
		 */
		OPERABLE,

		/**
		 * 就绪；
		 */
		PREPARED,

		/**
		 * 已提交；
		 */
		COMMITTED,

		/**
		 * 已关闭；
		 */
		CLOSED

	}

}
