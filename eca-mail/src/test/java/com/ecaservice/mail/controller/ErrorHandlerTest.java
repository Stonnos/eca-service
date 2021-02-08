package com.ecaservice.mail.controller;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.engine.path.PathImpl.createPathFromString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ErrorHandler} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    private static final String EMAIL_RECEIVER = "email.receiver";
    private static final String ERROR_MESSAGE = "Invalid receiver!";

    private ErrorHandler errorHandler = new ErrorHandler();

    @Mock
    private ConstraintViolationException constraintViolationException;

    @Mock
    private ConstraintViolation<?> constraintViolation;

    @BeforeEach
    void setUp() {
        when(constraintViolation.getPropertyPath()).thenReturn(createPathFromString(EMAIL_RECEIVER));
        when(constraintViolation.getMessage()).thenReturn(ERROR_MESSAGE);
        when(constraintViolationException.getConstraintViolations()).thenReturn(newHashSet(constraintViolation));
    }

    @Test
    void testHandleConstraintViolation() {
        ResponseEntity<List<ValidationErrorDto>> errorResponse =
                errorHandler.handleConstraintViolation(constraintViolationException);
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getBody()).hasSize(1);
        ValidationErrorDto validationErrorDto = errorResponse.getBody().iterator().next();
        assertThat(validationErrorDto.getFieldName()).isEqualTo("receiver");
        assertThat(validationErrorDto.getErrorMessage()).isEqualTo(ERROR_MESSAGE);
    }
}
