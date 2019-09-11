package com.jd.blockchain.utils.serialize.binary;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;

public class FilteredObjectInputStream extends ObjectInputStream {
	
	private static final Set<String> classBlacklist = new HashSet<String>();

	/**
	 * 把指定类型加入禁止反序列化的类型黑名单；
	 * 
	 * @param className
	 */
	public static void addBlackList(String className) {
		classBlacklist.add(className);
	}

	public FilteredObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
		if (classBlacklist.contains(desc.getName())) {
			throw new SecurityException("Class["+desc.getName()+"] is forbidden to deserialize because it is in the blacklist!");
		}
		return super.resolveClass(desc);
	}

}
