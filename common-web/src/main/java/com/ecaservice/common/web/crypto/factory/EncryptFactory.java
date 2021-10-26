package com.ecaservice.common.web.crypto.factory;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Encrypt factory class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class EncryptFactory {

    /**
     * Creates AES encryptor bean.
     *
     * @param password - password for PBKDF2WithHmacSHA1 algorithm
     * @param salt     - salt for PBKDF2WithHmacSHA1 algorithm
     * @return AES encryptor bean
     */
    public static AesBytesEncryptor getAesBytesEncryptor(String password, String salt) {
        Objects.requireNonNull(password, "Password must be not null!");
        Objects.requireNonNull(salt, "Salt must be not null!");
        char[] saltHex = Hex.encode(salt.getBytes(StandardCharsets.UTF_8));
        return new AesBytesEncryptor(password, new String(saltHex));
    }
}
