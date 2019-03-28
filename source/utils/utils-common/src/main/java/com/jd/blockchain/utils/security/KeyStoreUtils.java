package com.jd.blockchain.utils.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class KeyStoreUtils {
	
	public static final String P12_KEYSTORE_TYPE = "PKCS12";
	
	public static void validateP12(byte[] certicate, String password) throws KeyStoreException{
		try {
			ByteArrayInputStream certStream = new ByteArrayInputStream(certicate);
			char[] pwdChars = password.toCharArray();
			KeyStore ks = KeyStore.getInstance(P12_KEYSTORE_TYPE);
			ks.load(certStream, pwdChars);
		} catch (java.security.KeyStoreException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (CertificateException e) {
			throw new KeyStoreException(e.getMessage(), e);
		} catch (IOException e) {
			throw new KeyStoreException(e.getMessage(), e);
		}
	}
	
}
