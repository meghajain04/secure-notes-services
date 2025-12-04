package com.example.securenotes.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class EncryptionUtil {
    private static final String ALGO = "AES";

    @Value("${encryption.key}")
    private  String secretKey;

    private byte[] getKey() {
        // Lazy initialization ensures secretKey is injected
        return secretKey.getBytes(StandardCharsets.UTF_8);
    }

    public  String encrypt(String content) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        SecretKeySpec  keySpec = new SecretKeySpec(getKey(),ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(content.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public   String decrypt(String encrytContent) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        SecretKeySpec keySpec = new SecretKeySpec(getKey(),ALGO);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(encrytContent);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
