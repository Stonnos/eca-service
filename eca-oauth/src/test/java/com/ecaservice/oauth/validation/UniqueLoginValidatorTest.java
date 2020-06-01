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
 * Unit tests for checking {@link UniqueLoginValidator} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
public class UniqueLoginValidatorTest {

    private static final String USER_NAME = "user";

    @Mock
    private UserEntityRepository userEntityRepository;

    private UniqueLoginValidator uniqueLoginValidator;

    @BeforeEach
    void init() {
        uniqueLoginValidator = new UniqueLoginValidator(userEntityRepository);
    }

    @Test
    void testEmptyLogin() {
        assertThat(uniqueLoginValidator.isValid(StringUtils.EMPTY, null)).isFalse();
    }

    @Test
    void testLoginExists() {
        when(userEntityRepository.existsByLogin(USER_NAME)).thenReturn(true);
        assertThat(uniqueLoginValidator.isValid(USER_NAME, null)).isFalse();
    }

    @Test
    void testLoginNotExists() {
        when(userEntityRepository.existsByLogin(USER_NAME)).thenReturn(false);
        assertThat(uniqueLoginValidator.isValid(USER_NAME, null)).isTrue();
    }
}
