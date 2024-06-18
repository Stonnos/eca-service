package com.ecaservice.oauth.service;

import com.ecaservice.oauth.config.PasswordConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
class PasswordServiceTest {

    @Autowired
    private PasswordConfig passwordConfig;

    @Autowired
    private PasswordService passwordService;

    @Test
    void testGeneratedPasswordLength() {
        String password = passwordService.generatePassword();
        assertThat(password).isNotNull().hasSize(passwordConfig.getLength());
    }
}
