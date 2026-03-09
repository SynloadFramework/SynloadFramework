package com.synload.framework.ws;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

// TODO: Implement 256-bit version like: http://securejava.wordpress.com/2012/10/25/aes-256/
public class AesUtil {
    private final int keySize;
    private final int iterationCount;
    private final String cipherTransformation = "AES/CBC/PKCS5Padding";

    public AesUtil(int keySize, int iterationCount) {
        this.keySize = keySize;
        this.iterationCount = iterationCount;
        // Verify the cipher transformation is available at construction time
        try {
            Cipher.getInstance(cipherTransformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to instantiate cipher: " + cipherTransformation, e);
        }
    }

    private Cipher createCipher() {
        try {
            return Cipher.getInstance(cipherTransformation);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to instantiate cipher: " + cipherTransformation, e);
        }
    }

    public String encrypt(String salt, String iv, String passphrase,
            String plaintext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv,
                    plaintext.getBytes("UTF-8"));
            return base64(encrypted);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }
    public String encrypt(String salt, String iv, String passphrase,
            byte[] plaintext) {
        SecretKey key = generateKey(salt, passphrase);
        byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv,
                plaintext);
        return base64(encrypted);
    }

    public String decrypt(String salt, String iv, String passphrase,
            String ciphertext) {
        try {
            SecretKey key = generateKey(salt, passphrase);
            byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv,
                    base64(ciphertext));
            return new String(decrypted, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }
    public byte[] decryptByte(String salt, String iv, String passphrase,
            String ciphertext) {
        SecretKey key = generateKey(salt, passphrase);
        byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv,
                base64(ciphertext));
        return decrypted;
    }

    private byte[] doFinal(int encryptMode, SecretKey key, String iv,
            byte[] bytes) {
        try {
            Cipher cipher = createCipher();
            cipher.init(encryptMode, key, new IvParameterSpec(hex(iv)));
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Cipher operation failed", e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Invalid cipher parameters", e);
        }
    }

    private SecretKey generateKey(String salt, String passphrase) {
        try {
            SecretKeyFactory factory = SecretKeyFactory
                    .getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), hex(salt),
                    iterationCount, keySize);
            SecretKey key = new SecretKeySpec(factory.generateSecret(spec)
                    .getEncoded(), "AES");
            return key;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to generate secret key", e);
        }
    }

    public static String random(int length) {
        byte[] salt = new byte[length];
        new SecureRandom().nextBytes(salt);
        return hex(salt);
    }

    public static String base64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] base64(String str) {
        return Base64.decodeBase64(str);
    }

    public static String hex(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }

    public static byte[] hex(String str) {
        try {
            return Hex.decodeHex(str.toCharArray());
        } catch (DecoderException e) {
            throw new IllegalStateException(e);
        }
    }
}
