package com.jd.blockchain.utils;

import java.io.Closeable;

public interface ServiceFactory extends Closeable{

	<T> T getService(Class<T> serviceClazz);

}