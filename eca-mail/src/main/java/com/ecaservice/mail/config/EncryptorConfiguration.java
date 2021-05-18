package com.ecaservice.mail.config;

import com.ecaservice.mail.service.EncryptorBase64AdapterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

import java.nio.charset.StandardCharsets;

/**
 * Encryptor configuration.
 *
 * @author Roman Batygin
 */
@Configuration
public class EncryptorConfiguration {

    /**
     * Creates AES encryptor bean.
     *
     * @param mailConfig - mail config
     * @return AES encryptor bean
     */
    @Bean
    public AesBytesEncryptor aesBytesEncryptor(MailConfig mailConfig) {
        char[] saltHex = Hex.encode(mailConfig.getEncrypt().getSalt().getBytes(StandardCharsets.UTF_8));
        return new AesBytesEncryptor(mailConfig.getEncrypt().getPassword(), new String(saltHex));
    }

    /**
     * Creates encryptor Base64 adapter service bean.
     *
     * @param aesBytesEncryptor - AES encryptor bean
     * @return encryptor Base64 adapter service bean
     */
    @Bean
    public EncryptorBase64AdapterService encryptorBase64AdapterService(AesBytesEncryptor aesBytesEncryptor) {
        return new EncryptorBase64AdapterService(aesBytesEncryptor);
    }
}
