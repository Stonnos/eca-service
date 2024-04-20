package com.ecaservice.common.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.common.web.error.CommonErrorCode;
import com.ecaservice.common.web.exception.ValidationErrorException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExceptionResponseHandler} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final String EMAIL_RECEIVER = "email.receiver";
    private static final String ERROR_MESSAGE = "message";
    private static final String ERROR_CODE = "code";

    @Mock
    private ConstraintViolationException constraintViolationException;
    @Mock
    private ConstraintViolation<?> constraintViolation;
    @Mock
    private Path.Node node;
    @Mock
    private Path path;

    @Mock
    private MethodArgumentTypeMismatchException methodArgumentTypeMismatchException;
    @Mock
    private BindException bindException;

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleConstraintViolation() {
        mockConstraintViolation();
        var errorResponse = exceptionHandler.handleConstraintViolation(constraintViolationException);
        assertResponse(errorResponse, null, EMAIL_RECEIVER, ERROR_MESSAGE);
    }

    @Test
    void testValidationError() {
        var errorResponse =
                exceptionHandler.handleValidationError(new ValidationErrorException(CommonErrorCode.INVALID_FORMAT_CODE, ERROR_MESSAGE));
        assertResponse(errorResponse, CommonErrorCode.INVALID_FORMAT_CODE.getCode(), null, ERROR_MESSAGE);
    }

    @Test
    void testHttpMessageNotReadable() {
        var httpInputMessage = mock(HttpInputMessage.class);
        var invalidFormatException = mock(InvalidFormatException.class);
        var reference = new JsonMappingException.Reference(null, EMAIL_RECEIVER);
        var exception = new HttpMessageNotReadableException(ERROR_MESSAGE, invalidFormatException, httpInputMessage);
        when(invalidFormatException.getPath()).thenReturn(Collections.singletonList(reference));
        var errorResponse = exceptionHandler.handleHttpMessageNotReadableError(exception);
        assertResponse(errorResponse, "InvalidFormat", EMAIL_RECEIVER, exception.getMessage());
    }

    @Test
    void testBindException() {
        mockBindException();
        var errorResponse = exceptionHandler.handleBindException(bindException);
        assertResponse(errorResponse, null, EMAIL_RECEIVER, ERROR_MESSAGE);
    }

    @Test
    void testMethodArgumentMismatchError() {
        when(methodArgumentTypeMismatchException.getMessage()).thenReturn(ERROR_MESSAGE);
        when(methodArgumentTypeMismatchException.getErrorCode()).thenReturn(ERROR_CODE);
        var methodParameter = mock(MethodParameter.class);
        when(methodParameter.getParameterName()).thenReturn(EMAIL_RECEIVER);
        when(methodArgumentTypeMismatchException.getParameter()).thenReturn(methodParameter);
        var errorResponse =
                exceptionHandler.handleMethodArgumentTypeMismatchException(methodArgumentTypeMismatchException);
        assertResponse(errorResponse, ERROR_CODE, EMAIL_RECEIVER, ERROR_MESSAGE);
    }

    private void mockBindException() {
        var fieldError = new FieldError(Object.class.getSimpleName(), EMAIL_RECEIVER, ERROR_MESSAGE);
        when(bindException.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
    }

    private void mockConstraintViolation() {
        when(path.iterator()).thenReturn(new Iterator<>() {

            boolean next = true;

            @Override
            public boolean hasNext() {
                return next;
            }

            @Override
            public Path.Node next() {
                next = false;
                return node;
            }
        });
        when(node.getName()).thenReturn(EMAIL_RECEIVER);
        when(constraintViolation.getPropertyPath()).thenReturn(path);
        when(constraintViolation.getMessage()).thenReturn(ERROR_MESSAGE);
        when(constraintViolationException.getConstraintViolations()).thenReturn(newHashSet(constraintViolation));
    }

    private void assertResponse(ResponseEntity<List<ValidationErrorDto>> errorResponse, String expectedCode,
                                String expectedField, String expectedErrorMessage) {
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getBody()).hasSize(1);
        var validationErrorDto = errorResponse.getBody().iterator().next();
        assertThat(validationErrorDto.getCode()).isEqualTo(expectedCode);
        assertThat(validationErrorDto.getFieldName()).isEqualTo(expectedField);
        assertThat(validationErrorDto.getErrorMessage()).isEqualTo(expectedErrorMessage);
    }
}
