package com.chester.michat.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * RSA公钥/私钥/签名工具包
 * </p>
 * <p>
 * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
 * </p>
 * <p>
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 * </p>
 *
 * @author IceWee
 * @date 2012-4-26
 * @version 1.0
 */
public class RSAUtils {

	/**
	 * 加密算法RSA
	 */
	public static final String KEY_ALGORITHM = "RSA";

	/**
	 * 签名算法
	 */
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	/**
	 * 获取公钥的key
	 */
	private static final String PUBLIC_KEY = "RSAPublicKey";

	/**
	 * 获取私钥的key
	 */
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	/**
	 * RSA最大加密明文大小
	 */
	private static final int MAX_ENCRYPT_BLOCK = 117;

	/**
	 * RSA最大解密密文大小
	 */
	private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * 生成密钥对(公钥和私钥)
	 */
	public static Map<String, Key> genKeyPair(){
		KeyPairGenerator keyPairGen = null;
		try {
			keyPairGen = KeyPairGenerator
					.getInstance(KEY_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		Map<String, Key> keyMap = new HashMap<>(2);
		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	/**
	 * 用私钥对信息生成数字签名
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = Base64.encodeBase64(privateKey.getBytes());
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateK);
		signature.update(data);
		return Arrays.toString(Base64.encodeBase64(signature.sign()));
	}

	/**
	 * 校验数字签名
	 *
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicK = keyFactory.generatePublic(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(publicK);
		signature.update(data);
		return signature.verify(Base64.decodeBase64(sign));
	}

	/**
	 * 私钥解密
	 */
	public static byte[] decrypt(byte[] data, String privateKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateK);
		return crypt(data,cipher);
	}

	/**
	 * 公钥加密
	 */
	public static byte[] encrypt(byte[] data, String publicKey) throws Exception {
		byte[] keyBytes = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicK = keyFactory.generatePublic(x509KeySpec);
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicK);
		return crypt(data,cipher);
	}

	public static byte[] crypt(byte[] data,Cipher cipher)throws Exception{
		int inputLen = data.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段解密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
				cache = cipher
						.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
			} else {
				cache = cipher
						.doFinal(data, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_DECRYPT_BLOCK;
		}
		byte[] decryptedData = out.toByteArray();
		out.close();
		return decryptedData;
	}

	/**
	 * 获取私钥
	 */
	public static String getPrivateKey(Map<String, Key> keyMap) {
		Key key = keyMap.get(PRIVATE_KEY);
		return Base64.encodeBase64String(key.getEncoded());
	}

	/**
	 * 获取公钥
	 */
	public static String getPublicKey(Map<String, Key> keyMap) {
		Key key = keyMap.get(PUBLIC_KEY);
		return Base64.encodeBase64String(key.getEncoded());
	}

	public static final Map<String, String> KEYS = new HashMap<>();


	/**
	 * 获取私钥
	 */
	public static String getPrivateKey() {
		return KEYS.get(PRIVATE_KEY);
	}

	/**
	 * 获取公钥
	 */
	public static String getPublicKey() {
		return KEYS.get(PUBLIC_KEY);
	}

	static {
		KEYS.put(PUBLIC_KEY,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIJj3enNQ6nUqEQHxsXGenHYsDQrxfyMNcOpXt9vAwv9XVR1ZaASmeZlil66Gy3ppE+8N57d0UlpyYkjmN0F+B/h7D3DuWluFz/hXHmoyIN+SeThjIsXSlVaR15qNTBn0XydWxZvZsnMs5LNzRBxlxPKYOJ4PLlODuw82h2BnPqwIDAQAB");
		KEYS.put(PRIVATE_KEY,"MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIgmPd6c1DqdSoRAfGxcZ6cdiwNCvF/Iw1w6le328DC/1dVHVloBKZ5mWKXrobLemkT7w3nt3RSWnJiSOY3QX4H+HsPcO5aW4XP+FceajIg35J5OGMixdKVVpHXmo1MGfRfJ1bFm9mycyzks3NEHGXE8pg4ng8uU4O7DzaHYGc+rAgMBAAECgYAT8x6PtwU2DYQ0uspAio25pA+Ri77UfjN6GS+M7ekmC4YGuWxCxfbWOgL1VQCdmBac0xNCk4MYmDUb+hkq2YSU7cA9+GnKyu7N6KRocEHSkyvn8RCn46K2a/f7sgdodfY++0Hn4vjC6hjVyhokmk0qLN5aG61xh/FdJKhy2DN+WQJBAMt7sVuFSFdnGYlcvh8wao2q9ozlKldrO2htyQYpkt3pV1Y1NGh0Ra2Wi651MzU3ejOoZ/8ZrBH+DXy3df4dZe0CQQCrSb9kKtm0ffusdPnlSwhI/Uw8xhSnVHSZM84eljVrLXBG0TdCXP5PlRPIke5el3E1mlIadSCe4VuxMpozH1j3AkAzStLCRy6dGZgOUBp8si+p1laf3YR21Mj84e+yZfhnh9JEINU9H1IRF8nOCC0IVRuwudq9KzN8tZWgwn4zuCVhAkATsDVkoRxHB9ge2Y9D0N0R7rONpMMI/McuOOsvTtqzCBUIvtGYOjItGZidjFCW5NdpbuSTD/WA7axpcMSSrb6/AkBrdq+3pOaMJkX5xIsZtoNAAY15Y4H1qHKY+mnfnpVwJ3tiyG5a6ZFqDqq5QkyknOKKJoS+drincm2NvwQ5YLZ4");
	}

}
