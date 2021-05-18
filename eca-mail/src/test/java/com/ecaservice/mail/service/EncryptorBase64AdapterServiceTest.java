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
 * Unit tests for {@link EncryptorBase64AdapterService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({EncryptorConfiguration.class, MailConfig.class})
class EncryptorBase64AdapterServiceTest {

    private static final String TEST_MESSAGE = "Test message";

    @Inject
    private EncryptorBase64AdapterService encryptorBase64AdapterService;

    @Test
    void testEncryptMessage() {
        String cipher = encryptorBase64AdapterService.encrypt(TEST_MESSAGE);
        assertThat(cipher).isNotNull();
        String encoded = encryptorBase64AdapterService.decrypt(cipher);
        assertThat(encoded).isEqualTo(TEST_MESSAGE);
    }
}
