package com.ecaservice.common.web.crypto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

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
        return Base64Utils.encodeToString(cipherBytes);
    }

    /**
     * Encrypt message.
     *
     * @param cipher - cipher value in Base64
     * @return encoded message
     */
    public String decrypt(String cipher) {
        byte[] cipherBytes = Base64Utils.decodeFromString(cipher);
        byte[] encodedBytes = bytesEncryptor.decrypt(cipherBytes);
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }
}
