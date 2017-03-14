package com.dali.store.securiy;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * @author Will WM. Zhang
 * 
 */
public class HashUtil {

	public static final String MD2 = "MD2";
	public static final String MD5 = "MD5";
	public static final String SHA1 = "SHA-1";
	public static final String SHA224 = "SHA-224";
	public static final String SHA256 = "SHA-256";
	public static final String SHA384 = "SHA-384";
	public static final String SHA512 = "SHA-512";

	public static final int DEFAULT_ITERATIONS = 1;

	public static String md2Hex(String str) {
		return md2Hex(str, null, DEFAULT_ITERATIONS);
	}

	public static String md2Hex(String str, Object salt) {
		return md2Hex(str, salt, DEFAULT_ITERATIONS);
	}

	public static String md2Hex(String str, Object salt, int hashIterations) {
		return new SimpleHash(MD2, str, salt, hashIterations).toHex();
	}

	public static String md2Base64(String str) {
		return md2Base64(str, null, DEFAULT_ITERATIONS);
	}

	public static String md2Base64(String str, Object salt) {
		return md2Base64(str, salt, DEFAULT_ITERATIONS);
	}

	public static String md2Base64(String str, Object salt, int hashIterations) {
		return new SimpleHash(MD2, str, salt, hashIterations).toBase64();
	}

	public static String md5Hex(String str) {
		return md5Hex(str, null, DEFAULT_ITERATIONS);
	}

	public static String md5Hex(String str, Object salt) {
		return md5Hex(str, salt, DEFAULT_ITERATIONS);
	}

	public static String md5Hex(String str, Object salt, int hashIterations) {
		return new SimpleHash(MD5, str, salt, hashIterations).toHex();
	}

	public static String md5Base64(String str) {
		return md5Base64(str, null, DEFAULT_ITERATIONS);
	}

	public static String md5Base64(String str, Object salt) {
		return md5Base64(str, null, DEFAULT_ITERATIONS);
	}

	public static String md5Base64(String str, Object salt, int hashIterations) {
		return new SimpleHash(MD5, str, salt, hashIterations).toBase64();
	}

	public static String sha1Hex(String str) {
		return sha1Hex(str, null, DEFAULT_ITERATIONS);
	}

	public static String sha1Hex(String str, Object salt) {
		return sha1Hex(str, salt, DEFAULT_ITERATIONS);
	}

	public static String sha1Hex(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA1, str, salt, hashIterations).toHex();
	}

	public static String sha1Base64(String str) {
		return sha1Base64(str, null, DEFAULT_ITERATIONS);
	}

	public static String sha1Base64(String str, Object salt) {
		return sha1Base64(str, salt, DEFAULT_ITERATIONS);
	}

	public static String sha1Base64(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA1, str, salt, hashIterations).toBase64();
	}

	public static String sha224Hex(String str) {
		return sha224Hex(str, null, DEFAULT_ITERATIONS);
	}

	public static String sha224Hex(String str, Object salt) {
		return sha224Hex(str, salt, DEFAULT_ITERATIONS);
	}

	public static String sha224Hex(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA224, str, salt, hashIterations).toHex();
	}

	public static String sha224Base64(String str) {
		return sha224Base64(str, null, DEFAULT_ITERATIONS);
	}

	public static String sha224Base64(String str, Object salt) {
		return sha224Base64(str, salt, DEFAULT_ITERATIONS);
	}

	public static String sha224Base64(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA224, str, salt, hashIterations).toBase64();
	}
	
	public static String sha256Hex(String str) {
		return sha256Hex(str, null, DEFAULT_ITERATIONS);
	}
	
	public static String sha256Hex(String str, Object salt) {
		return sha256Hex(str, salt, DEFAULT_ITERATIONS);
	}

	public static String sha256Hex(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA256, str, salt, hashIterations).toHex();
	}
	
	public static String sha256Base64(String str) {
		return sha256Base64(str, null, DEFAULT_ITERATIONS);
	}
	
	public static String sha256Base64(String str, Object salt) {
		return sha256Base64(str, salt, DEFAULT_ITERATIONS);
	}
	
	public static String sha256Base64(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA256, str, salt, hashIterations).toBase64();
	}
	
	public static String sha384Hex(String str) {
		return sha384Hex(str, null, DEFAULT_ITERATIONS);
	}
	
	public static String sha384Hex(String str, Object salt) {
		return sha384Hex(str, salt, DEFAULT_ITERATIONS);
	}
	
	public static String sha384Hex(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA384, str, salt, hashIterations).toHex();
	}
	
	public static String sha384Base64(String str) {
		return sha384Base64(str, null, DEFAULT_ITERATIONS);
	}
	
	public static String sha384Base64(String str, Object salt) {
		return sha384Base64(str, salt, DEFAULT_ITERATIONS);
	}
	
	public static String sha384Base64(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA384, str, salt, hashIterations).toBase64();
	}
	
	public static String sha512Hex(String str) {
		return sha512Hex(str, null, DEFAULT_ITERATIONS);
	}
	
	public static String sha512Hex(String str, Object salt) {
		return sha512Hex(str, salt, DEFAULT_ITERATIONS);
	}

	public static String sha512Hex(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA512, str, salt, hashIterations).toHex();
	}
	
	public static String sha512Base64(String str) {
		return sha512Base64(str, null, DEFAULT_ITERATIONS);
	}
	
	public static String sha512Base64(String str, Object salt) {
		return sha512Base64(str, salt, DEFAULT_ITERATIONS);
	}
	
	public static String sha512Base64(String str, Object salt, int hashIterations) {
		return new SimpleHash(SHA512, str, salt, hashIterations).toBase64();
	}
}
