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
 * Unit tests for checking {@link UniqueEmailValidator} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class UniqueEmailValidatorTest {

    private static final String EMAIL = "test@mail.ru";

    @Mock
    private UserEntityRepository userEntityRepository;

    private UniqueEmailValidator uniqueEmailValidator;

    @BeforeEach
    void init() {
        uniqueEmailValidator = new UniqueEmailValidator(userEntityRepository);
    }

    @Test
    void testEmptyEmail() {
        assertThat(uniqueEmailValidator.isValid(StringUtils.EMPTY, null)).isFalse();
    }

    @Test
    void testEmailExists() {
        when(userEntityRepository.existsByEmail(EMAIL)).thenReturn(true);
        assertThat(uniqueEmailValidator.isValid(EMAIL, null)).isFalse();
    }

    @Test
    void testEmailNotExists() {
        when(userEntityRepository.existsByEmail(EMAIL)).thenReturn(false);
        assertThat(uniqueEmailValidator.isValid(EMAIL, null)).isTrue();
    }
}
