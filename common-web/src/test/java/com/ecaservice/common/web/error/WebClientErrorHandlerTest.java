package com.ecaservice.common.web.error;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link WebClientErrorHandler} class.
 *
 * @author Roman Batygin
 */
class WebClientErrorHandlerTest {

    private static final String INVALID_RESPONSE = "response";
    private static final String INVALID_ERROR_CODE = "abc";
    private final WebClientErrorHandler webClientErrorHandler = new WebClientErrorHandler();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testValidErrorCodes() throws JsonProcessingException {
        for (CommonErrorCode expectedErrorCode : CommonErrorCode.values()) {
            var validationErrorJsonString = createValidationErrorJsonString(expectedErrorCode.getCode());
            var actualErrorCode =
                    webClientErrorHandler.handleBadRequest(UUID.randomUUID().toString(), validationErrorJsonString,
                            CommonErrorCode.class);
            assertThat(actualErrorCode).isNotNull();
            assertThat(actualErrorCode).isEqualTo(expectedErrorCode);
        }
    }

    @Test
    void testInvalidErrorCodes() throws JsonProcessingException {
        var validationErrorJsonString = createValidationErrorJsonString(INVALID_ERROR_CODE);
        assertThrows(IllegalStateException.class,
                () -> webClientErrorHandler.handleBadRequest(UUID.randomUUID().toString(), validationErrorJsonString,
                        CommonErrorCode.class));
    }

    @Test
    void testInvalidResponse() {
        assertThrows(IllegalStateException.class,
                () -> webClientErrorHandler.handleBadRequest(UUID.randomUUID().toString(), INVALID_RESPONSE,
                        CommonErrorCode.class));
    }

    private String createValidationErrorJsonString(String errorCode) throws JsonProcessingException {
        var validationError = new ValidationErrorDto();
        validationError.setCode(errorCode);
        return objectMapper.writeValueAsString(Collections.singletonList(validationError));
    }
}
