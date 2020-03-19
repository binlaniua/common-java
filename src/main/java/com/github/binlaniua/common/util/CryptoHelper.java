package com.github.binlaniua.common.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * Tkk
 */
public class CryptoHelper {

    // 加密算法
    private static final String ALG = "AES";
    // 字符编码
    private static final String ENC = "UTF-8";
    // 密钥正规化算法
    private static final String SEC_NORMALIZE_ALG = "MD5";

    // 加密
    public static String aesEncrypt(String secret, String data) throws Exception {
        MessageDigest dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
        byte[] key = dig.digest(secret.getBytes(ENC));
        SecretKeySpec secKey = new SecretKeySpec(key, ALG);

        Cipher aesCipher = Cipher.getInstance(ALG);
        byte[] byteText = data.getBytes(ENC);

        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] byteCipherText = aesCipher.doFinal(byteText);

        Base64 base64 = new Base64();
        return new String(base64.encode(byteCipherText), ENC);
    }

    // 解密
    public static String aesDecrypt(String secret, String ciphertext) throws Exception {
        MessageDigest dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
        byte[] key = dig.digest(secret.getBytes(ENC));
        SecretKeySpec secKey = new SecretKeySpec(key, ALG);

        Cipher aesCipher = Cipher.getInstance(ALG);
        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        Base64 base64 = new Base64();
        byte[] cipherbytes = base64.decode(ciphertext.getBytes());
        byte[] bytePlainText = aesCipher.doFinal(cipherbytes);
        return new String(bytePlainText, ENC);
    }
}
