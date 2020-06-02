package com.ecaservice.oauth.validation;

import com.ecaservice.oauth.repository.UserEntityRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link UserEmailValidator} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class UserEmailValidatorTest {

    private static final String EMAIL = "test@mail.ru";

    @Mock
    private UserEntityRepository userEntityRepository;

    private UserEmailValidator userEmailValidator;

    @BeforeEach
    void init() {
        userEmailValidator = new UserEmailValidator(userEntityRepository);
    }

    @Test
    void testEmptyEmail() {
        assertThat(userEmailValidator.isValid(StringUtils.EMPTY, null)).isFalse();
    }

    @Test
    void testEmailExists() {
        when(userEntityRepository.existsByEmail(EMAIL)).thenReturn(true);
        assertThat(userEmailValidator.isValid(EMAIL, null)).isTrue();
    }

    @Test
    void testEmailNotExists() {
        when(userEntityRepository.existsByEmail(EMAIL)).thenReturn(false);
        assertThat(userEmailValidator.isValid(EMAIL, null)).isFalse();
    }
}
