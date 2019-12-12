package com.jd.blockchain.utils.serialize.json;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericType<T> {
	
	private final Type firstTypeArgument;

    protected GenericType(){
        Type superClass = getClass().getGenericSuperclass();

        firstTypeArgument = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

	public Type getTypeArgument() {
		return firstTypeArgument;
	}
	
}
