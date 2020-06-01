package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.PasswordConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.passay.PasswordGenerator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link PasswordService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({PasswordConfig.class, PasswordGenerator.class, PasswordService.class})
public class PasswordServiceTest {

    @Inject
    private PasswordConfig passwordConfig;

    @Inject
    private PasswordService passwordService;

    @Test
    void testGeneratedPasswordLength() {
        String password = passwordService.generatePassword();
        assertThat(password).isNotNull();
        assertThat(password.length()).isEqualTo(passwordConfig.getLength());
    }
}
