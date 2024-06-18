package com.ecaservice.common.web.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Adapter service to encrypt messages in base64 cipher format.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class EncryptorBase64AdapterService {

    private final BytesEncryptor bytesEncryptor;

    /**
     * Encrypt message as cipher in Base64.
     *
     * @param message - message value
     * @return cipher value in Base64 format
     */
    public String encrypt(String message) {
        byte[] cipherBytes = bytesEncryptor.encrypt(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(cipherBytes);
    }

    /**
     * Encrypt message.
     *
     * @param cipher - cipher value in Base64
     * @return encoded message
     */
    public String decrypt(String cipher) {
        byte[] cipherBytes = Base64.getDecoder().decode(cipher);
        byte[] encodedBytes = bytesEncryptor.decrypt(cipherBytes);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }
}
