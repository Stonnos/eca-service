package com.ecaservice.mail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

/**
 * AES encryptor service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class AesEncryptorService {

    private final AesBytesEncryptor aesBytesEncryptor;

    /**
     * Encrypt message.
     *
     * @param message - message value
     * @return cipher value in Base64 format
     */
    public String encrypt(String message) {
        byte[] cipherBytes = aesBytesEncryptor.encrypt(message.getBytes(StandardCharsets.UTF_8));
        return Base64Utils.encodeToString(cipherBytes);
    }

    /**
     * Encrypt message.
     *
     * @param cipher - cipher value
     * @return encoded message
     */
    public String decrypt(String cipher) {
        byte[] cipherBytes = Base64Utils.decodeFromString(cipher);
        byte[] encodedBytes = aesBytesEncryptor.decrypt(cipherBytes);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }
}
