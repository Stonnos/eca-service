package com.ecaservice.oauth.service;

import com.ecaservice.oauth.model.PasswordRuleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link PasswordValidationService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({PasswordRuleHandler.class, PasswordValidationService.class, ObjectMapper.class})
class PasswordValidationServiceTest {

    @Inject
    private PasswordValidationService passwordValidationService;

    @Test
    void testValidatePassword() {
        var result = passwordValidationService.validate("#123dCgrh56$f");
        assertThat(result).isNotNull();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void testValidatePasswordWithRepeatCharacters() {
        testNotValidRule("#1223dCgrh56$f", PasswordRuleType.REPEAT_CHARACTERS);
    }

    @Test
    void testValidatePasswordWithInvalidLength() {
        testNotValidRule("#123dCh56$f", PasswordRuleType.MIN_LENGTH);
    }

    @Test
    void testValidatePasswordWithoutLowerCaseCharacters() {
        testNotValidRule("#1223CFECXDFS56$", PasswordRuleType.LOWER_CASE_CHARACTER);
    }

    @Test
    void testValidatePasswordWithoutDigits() {
        testNotValidRule("#DELDCFECdwXDFSDV$", PasswordRuleType.DIGIT);
    }

    @Test
    void testValidatePasswordWithoutSpecialCharacter() {
        testNotValidRule("DELDCFECdwXDFSDV5", PasswordRuleType.SPECIAL_CHARACTER);
    }

    @Test
    void testValidatePasswordWithoutUpperCaseCharacters() {
        testNotValidRule("#104859fgf84bvr$", PasswordRuleType.UPPER_CASE_CHARACTER);
    }

    private void testNotValidRule(String password, PasswordRuleType expectedNotValidRuleType) {
        var result = passwordValidationService.validate(password);
        var details = result.getDetails().stream()
                .filter(ruleResultDetails -> ruleResultDetails.getRuleType().equals(expectedNotValidRuleType))
                .findFirst()
                .orElse(null);
        assertThat(details).isNotNull();
        assertThat(details.isValid()).isFalse();
    }
}
