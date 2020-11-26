package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.dto.ValidationErrorDto;
import com.ecaservice.external.api.metrics.MetricsService;
import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ecaservice.external.api.util.Utils.buildResponse;

/**
 * Exception handler for controllers.
 *
 * @author Roman Batygin
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ErrorHandler {

    private final MetricsService metricsService;

    /**
     * Handles validation error.
     *
     * @param ex -  method argument not valid exception
     * @return response entity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {
        return handleValidationError(() -> ex.getBindingResult().getAllErrors()
                .stream()
                .map(FieldError.class::cast)
                .map(fieldError -> new ValidationErrorDto(fieldError.getField(),
                        fieldError.getDefaultMessage())).collect(Collectors.toList()));
    }

    /**
     * Handles constraint violation error.
     *
     * @param ex - constraint violation exception
     * @return response entity
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleConstraintViolation(
            ConstraintViolationException ex) {
        return handleValidationError(() -> ex.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    Path.Node node = Iterables.getLast(constraintViolation.getPropertyPath());
                    ValidationErrorDto validationErrorDto = new ValidationErrorDto();
                    validationErrorDto.setFieldName(node.getName());
                    validationErrorDto.setErrorMessage(constraintViolation.getMessage());
                    return validationErrorDto;
                }).collect(Collectors.toList()));
    }

    private ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleValidationError(
            Supplier<List<ValidationErrorDto>> supplier) {
        metricsService.trackRequestsTotal();
        List<ValidationErrorDto> errors = supplier.get();
        ResponseDto<List<ValidationErrorDto>> responseDto = buildResponse(RequestStatus.VALIDATION_ERROR, errors);
        metricsService.trackRequestStatus(responseDto.getRequestStatus());
        metricsService.trackResponsesTotal();
        return ResponseEntity.badRequest().body(responseDto);
    }
}
