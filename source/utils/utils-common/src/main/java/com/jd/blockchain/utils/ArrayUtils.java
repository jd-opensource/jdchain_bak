package com.jd.blockchain.utils;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author haiq
 *
 */
public abstract class ArrayUtils {
	private ArrayUtils() {

	}

	public static <T, R> R[] castTo(T[] objs, Class<R> clazz, CastFunction<T, R> cf) {
		if (objs == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		R[] array = (R[]) Array.newInstance(clazz, objs.length);
		for (int i = 0; i < objs.length; i++) {
			array[i] = cf.cast(objs[i]);
		}
		return array;
	}

	public static <T> T[] singleton(T obj, Class<T> clazz) {
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(clazz, 1);
		array[0] = obj;
		return array;
	}

	public static <T> T[] toArray(Iterator<T> itr, Class<T> clazz) {
		List<T> lst = new LinkedList<T>();
		while (itr.hasNext()) {
			T t = (T) itr.next();
			lst.add(t);
		}
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(clazz, lst.size());
		lst.toArray(array);
		return array;
	}

	public static <T> T[] toArray(Collection<T> collection, Class<T> clazz) {
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(clazz, collection.size());
		collection.toArray(array);
		return array;
	}

	public static <T> List<T> asList(T[] array) {
		return asList(array, 0, array.length);
	}

	public static <T> Set<T> asSet(T[] array) {
		if (array == null || array.length == 0) {
			return Collections.emptySet();
		}
		HashSet<T> set = new HashSet<T>();
		for (T t : array) {
			set.add(t);
		}
		return set;
	}

	public static <T> SortedSet<T> asSortedSet(T[] array) {
		if (array == null || array.length == 0) {
			return Collections.emptySortedSet();
		}
		TreeSet<T> set = new TreeSet<T>();
		for (T t : array) {
			set.add(t);
		}
		return set;
	}

	public static <T> List<T> asList(T[] array, int fromIndex) {
		return asList(array, fromIndex, array.length);
	}

	public static <T> List<T> asList(T[] array, int fromIndex, int toIndex) {
		if (toIndex < fromIndex) {
			throw new IllegalArgumentException("The toIndex less than fromIndex!");
		}
		if (fromIndex < 0) {
			throw new IllegalArgumentException("The fromIndex is negative!");
		}
		if (toIndex > array.length) {
			throw new IllegalArgumentException("The toIndex great than the length of array!");
		}

		if (fromIndex == toIndex) {
			return Collections.emptyList();
		}
		return new ReadonlyArrayListWrapper<T>(array, fromIndex, toIndex);
	}

	public static interface CastFunction<T, R> {
		public R cast(T data);
	}

	/**
	 * Reverse all elements of the specified array; <br>
	 * 
	 * @param <T>
	 * @param array
	 */
	public static <T> void reverse(T[] array) {
		if (array == null || array.length < 2) {
			return;
		}
		
		T t;
		for (int i = 0, j = array.length - 1; i < j; i++, j--) {
			t = array[i];
			array[i] = array[j];
			array[j] = t;
		}
	}
}
