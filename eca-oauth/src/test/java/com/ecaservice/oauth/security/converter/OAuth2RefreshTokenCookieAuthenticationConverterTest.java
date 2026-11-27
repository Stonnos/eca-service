package com.ecaservice.oauth.security.converter;

import com.ecaservice.common.error.model.ErrorDetails;
import com.ecaservice.common.error.model.ErrorDto;
import com.ecaservice.oauth.controller.ErrorHandler;
import com.ecaservice.oauth.error.EcaOauthErrorCode;
import com.ecaservice.oauth.exception.NotSafePasswordException;
import com.ecaservice.oauth.exception.UserLockNotAllowedException;
import com.ecaservice.oauth.model.PasswordRuleType;
import com.ecaservice.oauth.model.RuleResultDetails;
import com.ecaservice.web.dto.model.PasswordRuleResultDto;
import com.ecaservice.web.dto.model.PasswordValidationErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ErrorHandler} class.
 *
 * @author Roman Batygin
 */
class ErrorHandlerTest {

    private static final String PASSWORD_ERROR_MESSAGE = "Password is not safe";
    private static final String PASSWORD_FIELD_NAME = "password";
    private static final String PASSWORD_ERROR_CODE = "NOT_SAFE_PASSWORD";
    private static final String RULE_MESSAGE = "Minimum length is 8";

    private ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void testHandleUserLockNotAllowed() {
        UserLockNotAllowedException exception = new UserLockNotAllowedException();
        ResponseEntity<ErrorDto> response = errorHandler.handleUserLockNotAllowed(exception);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(EcaOauthErrorCode.USER_LOCK_NOT_ALLOWED.getCode());
    }

    @Test
    void testHandleNotSafePasswordException() {
        RuleResultDetails ruleResultDetails = RuleResultDetails.builder()
                .ruleType(PasswordRuleType.MIN_LENGTH)
                .valid(false)
                .message(RULE_MESSAGE)
                .build();

        ErrorDetails errorDetails = mock(ErrorDetails.class);
        when(errorDetails.getCode()).thenReturn(PASSWORD_ERROR_CODE);

        NotSafePasswordException exception = mock(NotSafePasswordException.class);
        when(exception.getMessage()).thenReturn(PASSWORD_ERROR_MESSAGE);
        when(exception.getFieldName()).thenReturn(PASSWORD_FIELD_NAME);
        when(exception.getErrorDetails()).thenReturn(errorDetails);
        when(exception.getDetails()).thenReturn(List.of(ruleResultDetails));

        ResponseEntity<List<PasswordValidationErrorDto>> response =
                errorHandler.handleNotSafePasswordException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).hasSize(1);

        PasswordValidationErrorDto errorDto = response.getBody().getFirst();
        assertThat(errorDto.getCode()).isEqualTo(PASSWORD_ERROR_CODE);
        assertThat(errorDto.getFieldName()).isEqualTo(PASSWORD_FIELD_NAME);
        assertThat(errorDto.getErrorMessage()).isEqualTo(PASSWORD_ERROR_MESSAGE);

        assertThat(errorDto.getDetails()).hasSize(1);
        PasswordRuleResultDto ruleResultDto = errorDto.getDetails().getFirst();
        assertThat(ruleResultDto.getRule()).isEqualTo(PasswordRuleType.MIN_LENGTH.name());
        assertThat(ruleResultDto.isValid()).isFalse();
        assertThat(ruleResultDto.getMessage()).isEqualTo(RULE_MESSAGE);
    }
}
