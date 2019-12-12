package com.jd.blockchain.utils.concurrent;

public interface AsyncFutureListener<V> {

	public void complete(AsyncFuture<V> future);

}
