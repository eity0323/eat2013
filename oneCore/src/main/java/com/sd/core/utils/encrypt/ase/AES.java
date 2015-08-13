package com.sd.core.utils.encrypt.ase;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

	public static String encrypt(String data1) throws Exception {
		try {
			String data = data1;
			String key = "1234567812345678";
			String iv = "1234567812345678";

			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			int blockSize = cipher.getBlockSize();

			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}

			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			cipher.init(Cipher.ENCRYPT_MODE, keyspec);
			byte[] encrypted = cipher.doFinal(plaintext);

			return Base64.encode(encrypted);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String data1) throws Exception {
		try {
			String data = data1;
			String key = "1234567812345678";
			String iv = "1234567812345678";

			byte[] encrypted1 = Base64.decode(data);

			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			cipher.init(Cipher.DECRYPT_MODE, keyspec);

			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original);
			return originalString;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}