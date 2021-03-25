package com.ecaservice.common.web;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.common.web.exception.ValidationErrorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
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
    private MethodArgumentNotValidException methodArgumentNotValidException;
    @Mock
    private BindingResult bindingResult;

    private GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleConstraintViolation() {
        mockConstraintViolation();
        ResponseEntity<List<ValidationErrorDto>> errorResponse =
                exceptionHandler.handleConstraintViolation(constraintViolationException);
        assertResponse(errorResponse, null, EMAIL_RECEIVER, ERROR_MESSAGE);
    }

    @Test
    void testHandleMethodArgumentNotValid() {
        mockMethodArgumentNotValid();
        ResponseEntity<List<ValidationErrorDto>> errorResponse =
                exceptionHandler.handleMethodArgumentNotValid(methodArgumentNotValidException);
        assertResponse(errorResponse, null, EMAIL_RECEIVER, ERROR_MESSAGE);
    }

    @Test
    void testValidationError() {
        ResponseEntity<List<ValidationErrorDto>> errorResponse =
                exceptionHandler.handleValidationError(new ValidationErrorException(ERROR_CODE, ERROR_MESSAGE));
        assertResponse(errorResponse, ERROR_CODE, null, null);
    }

    @Test
    void testBadRequest() {
        ResponseEntity<String> responseEntity = exceptionHandler.handleBadRequest(new IllegalStateException());
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private void mockMethodArgumentNotValid() {
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        FieldError fieldError = new FieldError(Object.class.getSimpleName(), EMAIL_RECEIVER, ERROR_MESSAGE);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));
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
        ValidationErrorDto validationErrorDto = errorResponse.getBody().iterator().next();
        assertThat(validationErrorDto.getCode()).isEqualTo(expectedCode);
        assertThat(validationErrorDto.getFieldName()).isEqualTo(expectedField);
        assertThat(validationErrorDto.getErrorMessage()).isEqualTo(expectedErrorMessage);
    }
}
