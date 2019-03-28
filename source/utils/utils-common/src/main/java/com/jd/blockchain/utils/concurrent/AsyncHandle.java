package com.jd.blockchain.utils.concurrent;

import java.util.function.BiConsumer;

public interface AsyncHandle<T> extends BiConsumer<T, Throwable> {
	
	void complete(T returnValue, Throwable error);
	
	
	@Override
	default void accept(T t, Throwable u) {
		complete(t, u);
	}
}
