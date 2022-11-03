package com.ecaservice.common.web;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.common.web.exception.ValidationErrorException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.Iterables;
import lombok.experimental.UtilityClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ExceptionResponseHandler {

    private static final String INVALID_REQUEST_CODE = "InvalidRequest";
    private static final String INVALID_FORMAT_CODE = "InvalidFormat";
    private static final String MAX_UPLOAD_SIZE_EXCEEDED_CODE = "MaxUploadSizeExceeded";

    private static final String POINT = ".";
    private static final String OPEN_BRACKET = "[";
    private static final String CLOSE_BRACKET = "]";

    /**
     * Handles validation error.
     *
     * @param ex -  method argument not valid exception
     * @return response entity
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getAllErrors()
                .stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(), fieldError.getCode(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles constraint violation error.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleConstraintViolation(ConstraintViolationException ex) {
        var validationErrors = ex.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    var node = Iterables.getLast(constraintViolation.getPropertyPath());
                    var validationErrorDto = new ValidationErrorDto();
                    validationErrorDto.setFieldName(node.getName());
                    var code = Optional.ofNullable(constraintViolation.getConstraintDescriptor())
                            .map(ConstraintDescriptor::getAnnotation)
                            .map(Annotation::annotationType)
                            .map(Class::getSimpleName).orElse(null);
                    validationErrorDto.setCode(code);
                    validationErrorDto.setErrorMessage(constraintViolation.getMessage());
                    return validationErrorDto;
                }).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(validationErrors);
    }

    /**
     * Handles http message not readable errors.
     *
     * @param ex - http message not readable exception
     * @return validation errors
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        List<ValidationErrorDto> validationErrors = new ArrayList<>();
        if (ex.getCause() instanceof InvalidFormatException) {
            var invalidFormatException = (InvalidFormatException) ex.getCause();
            var validationErrorDto = new ValidationErrorDto();
            validationErrorDto.setCode(INVALID_FORMAT_CODE);
            validationErrorDto.setFieldName(getPropertyPath(invalidFormatException.getPath()));
            validationErrorDto.setErrorMessage(ex.getMessage());
            validationErrors.add(validationErrorDto);
        } else {
            var validationErrorDto = new ValidationErrorDto();
            validationErrorDto.setCode(INVALID_REQUEST_CODE);
            validationErrorDto.setErrorMessage(ex.getMessage());
            validationErrors.add(validationErrorDto);
        }
        return ResponseEntity.badRequest().body(validationErrors);
    }

    /**
     * Handles bind error.
     *
     * @param ex -  bind exception
     * @return response entity
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleBindException(BindException ex) {
        var errors = ex.getAllErrors()
                .stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(), fieldError.getCode(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles method argument type mismatch exception.
     *
     * @param ex - exception object
     * @return response entity
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        var validationErrorDto = new ValidationErrorDto();
        validationErrorDto.setCode(ex.getErrorCode());
        String fieldName = Optional.of(ex.getParameter())
                .map(MethodParameter::getParameterName)
                .orElse(null);
        validationErrorDto.setFieldName(fieldName);
        validationErrorDto.setErrorMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(Collections.singletonList(validationErrorDto));
    }

    /**
     * Handles validation error exception.
     *
     * @param ex - exception object
     * @return response entity
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleValidationErrorException(ValidationErrorException ex) {
        var validationErrorDto = new ValidationErrorDto();
        validationErrorDto.setCode(ex.getErrorCode());
        validationErrorDto.setFieldName(ex.getFieldName());
        validationErrorDto.setErrorMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(Collections.singletonList(validationErrorDto));
    }

    /**
     * Handles max upload size exceeded error.
     *
     * @param ex - exception object
     * @return response entity
     */
    public static ResponseEntity<List<ValidationErrorDto>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex) {
        var validationError = new ValidationErrorDto();
        validationError.setCode(MAX_UPLOAD_SIZE_EXCEEDED_CODE);
        validationError.setErrorMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(Collections.singletonList(validationError));
    }

    private static String getPropertyPath(List<JsonMappingException.Reference> references) {
        StringBuilder stringBuilder = new StringBuilder();
        IntStream.range(0, references.size()).forEach(i -> {
            var reference = references.get(i);
            if (reference.getIndex() >= 0) {
                stringBuilder.append(OPEN_BRACKET).append(reference.getIndex()).append(CLOSE_BRACKET);
            } else {
                stringBuilder.append(reference.getFieldName());
            }
            int nextIndex = i + 1;
            if (nextIndex < references.size() && references.get(nextIndex).getIndex() < 0) {
                stringBuilder.append(POINT);
            }
        });
        return stringBuilder.toString();
    }
}
