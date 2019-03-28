package com.jd.blockchain.crypto;

import com.jd.blockchain.crypto.asymmetric.AsymmetricCryptography;
import com.jd.blockchain.crypto.hash.HashCryptography;
import com.jd.blockchain.crypto.symmetric.SymmetricCryptography;

public interface CryptoFactory {
	
	HashCryptography hashCryptography();
	
	AsymmetricCryptography asymmetricCryptography();
	
	SymmetricCryptography symmetricCryptography();
	
}
