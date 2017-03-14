package com.dali.store.securiy;

import java.security.MessageDigest;

/**
 * MD5摘要计算通用算法:获取MD5算法和转16进制字符�?
 * 
 */
public class SHA256Transcrypter implements Transcrypter {

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 转换字节数组�?6进制字串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字串
	 */

	public String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	@Override
	public String encrypt(String string) {
		// TODO Auto-generated method stub
		String resultString = null;
		try {
			resultString = new String(string);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {

		}
		return resultString;
	}

	@Override
	public String decrypt(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String[] args) {
		String s = "123";
		SHA256Transcrypter crypt = new SHA256Transcrypter();
		System.out.println(crypt.encrypt(s));
	}
}