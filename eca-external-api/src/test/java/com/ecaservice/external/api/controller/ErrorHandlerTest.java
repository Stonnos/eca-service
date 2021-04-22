package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.metrics.MetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.inject.Inject;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ErrorHandler} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ErrorHandler.class)
class ErrorHandlerTest {

    private static final String FIELD_NAME = "fieldName";
    private static final String DEFAULT_MESSAGE = "error";

    @MockBean
    private MetricsService metricsService;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @Mock
    private BindingResult bindingResult;

    @Inject
    private ErrorHandler errorHandler;

    @Test
    void testMethodArgumentNotValidException() {
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        var fieldError = new FieldError(Object.class.getSimpleName(), FIELD_NAME, DEFAULT_MESSAGE);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
        var actualResponse = errorHandler.handleMethodArgumentNotValid(methodArgumentNotValidException);
        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(actualResponse.getBody()).isNotNull();
        assertThat(actualResponse.getBody().getRequestStatus()).isEqualTo(RequestStatus.VALIDATION_ERROR);
        verify(metricsService, atLeastOnce()).trackResponsesTotal();
        verify(metricsService, atLeastOnce()).trackRequestStatus(RequestStatus.VALIDATION_ERROR);
    }
}
