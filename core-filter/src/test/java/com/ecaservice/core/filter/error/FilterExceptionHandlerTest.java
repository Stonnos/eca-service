package com.ecaservice.core.filter.error;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FilterExceptionHandler} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
class FilterExceptionHandlerTest {

    private static final String ERROR_MESSAGE = "ErrorMessage";
    private static final String FIELD_NAME = "fieldName";
    private static final String FIELD_NOT_FOUND_CODE = "FieldNotFound";

    private final FilterExceptionHandler filterExceptionHandler = new FilterExceptionHandler();

    @Mock
    private PropertyReferenceException propertyReferenceException;

    @BeforeEach
    void init() {
        when(propertyReferenceException.getPropertyName()).thenReturn(FIELD_NAME);
        when(propertyReferenceException.getMessage()).thenReturn(ERROR_MESSAGE);
    }

    @Test
    void testHandlePropertyReferenceException() {
        var response = filterExceptionHandler.handleError(propertyReferenceException);
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).hasSize(1);
        var expectedValidationError = response.getBody().iterator().next();
        assertThat(expectedValidationError).isNotNull();
        assertThat(expectedValidationError.getCode()).isEqualTo(FIELD_NOT_FOUND_CODE);
        assertThat(expectedValidationError.getErrorMessage()).isEqualTo(ERROR_MESSAGE);
        assertThat(expectedValidationError.getFieldName()).isEqualTo(FIELD_NAME);
    }
}
