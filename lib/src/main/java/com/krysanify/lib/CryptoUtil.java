package com.krysanify.lib;

import android.util.Base64;

import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.NonNull;

import static android.util.Base64.encodeToString;
import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.UTF_8;

class CryptoUtil {
    // fixme: use a more obfuscated non-readable password, maybe the app's signature?
    private static final char[] PASSWORD = "TotallyNotSecure".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final SecretKeyFactory FACTORY;
    private static final Cipher CIPHER;

    static {
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();//todo: a fallback algorithm
        }
        FACTORY = factory;

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();//todo: a fallback algorithm
        }
        CIPHER = cipher;
    }

    @NonNull
    static String encrypt(@NonNull String plaintext) {
        byte[] salt = new byte[20];
        byte[] ivBytes;
        byte[] encrypted;

        RANDOM.nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(PASSWORD, salt, 65556, 256);

        try {
            SecretKey secretKey = FACTORY.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            CIPHER.init(Cipher.ENCRYPT_MODE, secret);
            AlgorithmParameters params = CIPHER.getParameters();

            ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
            encrypted = CIPHER.doFinal(plaintext.getBytes(UTF_8));
        } catch (InvalidKeySpecException | InvalidKeyException | BadPaddingException |
                IllegalBlockSizeException | InvalidParameterSpecException e) {
            e.printStackTrace();
            // fallback to plaintext
            ivBytes = new byte[CIPHER.getBlockSize()];
            int length = ivBytes.length > PASSWORD.length ? PASSWORD.length : ivBytes.length;
            for (int i = 0; i < length; i++) ivBytes[i] = (byte) PASSWORD[i];
            encrypted = plaintext.getBytes(UTF_8);
        }

        //prepend salt and vi
        byte[] buffer = new byte[salt.length + ivBytes.length + encrypted.length];
        arraycopy(salt, 0, buffer, 0, salt.length);
        arraycopy(ivBytes, 0, buffer, salt.length, ivBytes.length);
        arraycopy(encrypted, 0, buffer, salt.length + ivBytes.length, encrypted.length);
        return encodeToString(buffer, Base64.NO_WRAP | Base64.URL_SAFE);
    }

    static String decrypt(@NonNull String encoded) {
        byte[] decoded = Base64.decode(encoded, Base64.NO_WRAP | Base64.URL_SAFE);
        byte[] salt = new byte[20];
        byte[] ivBytes = new byte[CIPHER.getBlockSize()];
        byte[] encrypted = new byte[decoded.length - salt.length - ivBytes.length];

        ByteBuffer buffer = ByteBuffer.wrap(decoded);
        buffer.get(salt, 0, salt.length);
        buffer.get(ivBytes, 0, ivBytes.length);
        buffer.get(encrypted, 0, encrypted.length);
        PBEKeySpec spec = new PBEKeySpec(PASSWORD, salt, 65556, 256);

        try {
            SecretKey secretKey = FACTORY.generateSecret(spec);
            SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");
            CIPHER.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
            encrypted = CIPHER.doFinal(encrypted);
        } catch (InvalidKeySpecException | InvalidAlgorithmParameterException |
                InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return new String(encrypted);
    }
}
