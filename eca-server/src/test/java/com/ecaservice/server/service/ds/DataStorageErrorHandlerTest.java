package com.ecaservice.server.service.ds;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.data.storage.dto.DsInternalApiErrorCode;
import com.ecaservice.server.exception.DataStorageException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link DataStorageErrorHandler} class.
 *
 * @author Roman Batygin
 */
class DataStorageErrorHandlerTest {

    private static final String INVALID_RESPONSE = "response";
    private static final String INVALID_ERROR_CODE = "abc";
    private final DataStorageErrorHandler dataStorageErrorHandler = new DataStorageErrorHandler();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testValidErrorCodes() throws JsonProcessingException {
        for (DsInternalApiErrorCode expectedDsErrorCode : DsInternalApiErrorCode.values()) {
            var badRequestEx = mockErrorCodeException(expectedDsErrorCode.getCode());
            var actualErrorCode = dataStorageErrorHandler.handleBadRequest(UUID.randomUUID().toString(), badRequestEx);
            assertThat(actualErrorCode).isNotNull();
            assertThat(actualErrorCode).isEqualTo(expectedDsErrorCode);
        }
    }

    @Test
    void testInvalidErrorCodes() throws JsonProcessingException {
        var badRequestEx = mockErrorCodeException(INVALID_ERROR_CODE);
        assertThrows(DataStorageException.class,
                () -> dataStorageErrorHandler.handleBadRequest(UUID.randomUUID().toString(), badRequestEx));
    }

    @Test
    void testInvalidResponse() {
        var badRequestEx = mock(FeignException.BadRequest.class);
        when(badRequestEx.contentUTF8()).thenReturn(INVALID_RESPONSE);
        assertThrows(DataStorageException.class,
                () -> dataStorageErrorHandler.handleBadRequest(UUID.randomUUID().toString(), badRequestEx));
    }

    private FeignException.BadRequest mockErrorCodeException(String errorCode) throws JsonProcessingException {
        var badRequestEx = mock(FeignException.BadRequest.class);
        var validationError = new ValidationErrorDto();
        validationError.setCode(errorCode);
        when(badRequestEx.contentUTF8()).thenReturn(
                objectMapper.writeValueAsString(Collections.singletonList(validationError)));
        return badRequestEx;
    }
}
