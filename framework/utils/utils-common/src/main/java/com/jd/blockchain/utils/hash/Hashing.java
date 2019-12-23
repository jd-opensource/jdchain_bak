package com.jd.blockchain.utils.hash;

import com.jd.blockchain.utils.hash.MurmurHash3;

public interface Hashing {
	
	public static final Hashing MURMUR3_HASH = new Hashing() {
		
		public int hash32(CharSequence id) {
			return MurmurHash3.murmurhash3_x86_32(id, 0, id.length(), 1024);
		}
	};
	
	public int hash32(CharSequence id);
	
	
}
