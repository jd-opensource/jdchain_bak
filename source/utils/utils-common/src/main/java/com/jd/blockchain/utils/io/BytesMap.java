package com.jd.blockchain.utils.io;

import java.util.Set;

public interface BytesMap<T> {

	Set<T> keySet();

	byte[] getValue(T key);
}