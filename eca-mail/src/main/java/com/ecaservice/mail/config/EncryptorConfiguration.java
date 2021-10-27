package com.ecaservice.mail.config;

import com.ecaservice.common.web.crypto.EncryptorBase64AdapterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;

import static com.ecaservice.common.web.crypto.factory.EncryptFactory.getAesBytesEncryptor;

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
        return getAesBytesEncryptor(mailConfig.getEncrypt().getPassword(), mailConfig.getEncrypt().getSalt());
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
