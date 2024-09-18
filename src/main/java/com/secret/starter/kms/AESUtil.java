package com.secret.starter.kms;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    private static final String key = "1234567890abcdef"; // 16位密钥

    public static String encrypt(String value) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedValue) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
        return new String(decryptedBytes);
    }

    public static void main(String[] args) {
        try {
            String originalValue = "test_dddd";
            String encryptedValue = encrypt(originalValue);
            System.out.println("Encrypted: " + encryptedValue);

            String decryptedValue = decrypt(encryptedValue);
            System.out.println("Decrypted: " + decryptedValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
