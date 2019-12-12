package com.jd.blockchain.utils;

import java.util.Iterator;

/**
 * IteratorWrapper 是一个针对迭代器的适配器；
 * 
 * @author haiq
 *
 * @param <T1> T1
 * @param <T2> T2
 */
public abstract class IteratorWrapper<T1, T2> implements Iterator<T1> {

	protected Iterator<T2> wrappedIterator;

	public IteratorWrapper(Iterator<T2> itr) {
		this.wrappedIterator = itr;
	}

	@Override
	public boolean hasNext() {
		return wrappedIterator.hasNext();
	}

	@Override
	public T1 next() {
		return wrap(wrappedIterator.next());
	}
	
	protected abstract T1 wrap(T2 t2);

}
