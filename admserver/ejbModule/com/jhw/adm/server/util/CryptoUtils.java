package com.jhw.adm.server.util;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class CryptoUtils {
	private static final String ALGORITHM = "AES";

	public static byte[] encrypt(byte[] seed, byte[] input) throws Exception {
		Key key = generateKey(seed);
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedValue = c.doFinal(input);
		return encryptedValue;
	}

	public static byte[] decrypt(byte[] seed, byte[] input) throws Exception {
		Key key = generateKey(seed);
		Cipher c = Cipher.getInstance(ALGORITHM);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedValue = c.doFinal(input);
		return decryptedValue;
	}

	private static Key generateKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
        kgen.init(128, new SecureRandom(seed));
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, ALGORITHM);
        return key;
	}
}