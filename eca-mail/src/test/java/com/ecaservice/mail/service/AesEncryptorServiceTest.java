package com.ecaservice.mail.service;

import com.ecaservice.mail.config.EncryptorConfiguration;
import com.ecaservice.mail.config.MailConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AesEncryptorService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({EncryptorConfiguration.class, AesEncryptorService.class, MailConfig.class})
class AesEncryptorServiceTest {

    private static final String TEST_MESSAGE = "Test message";

    @Inject
    private AesEncryptorService aesEncryptorService;

    @Test
    void testEncryptMessage() {
        String cipher = aesEncryptorService.encrypt(TEST_MESSAGE);
        assertThat(cipher).isNotNull();
        String encoded = aesEncryptorService.decrypt(cipher);
        assertThat(encoded).isEqualTo(TEST_MESSAGE);
    }
}
