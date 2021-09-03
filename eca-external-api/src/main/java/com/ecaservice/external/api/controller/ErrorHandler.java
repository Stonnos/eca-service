package com.ecaservice.external.api.controller;

import com.ecaservice.common.web.ExceptionResponseHandler;
import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.common.web.exception.ValidationErrorException;
import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.metrics.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.function.Supplier;

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
        return handleValidationError(() -> ExceptionResponseHandler.handleMethodArgumentNotValid(ex));
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
        return handleValidationError(() -> ExceptionResponseHandler.handleConstraintViolation(ex));
    }

    /**
     * Handles http message not readable error.
     *
     * @param ex - http message not readable exception
     * @return response entity
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {
        return handleValidationError(() -> ExceptionResponseHandler.handleHttpMessageNotReadable(ex));
    }

    /**
     * Handles bind error.
     *
     * @param ex - bind exception
     * @return response entity
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleBindException(BindException ex) {
        return handleValidationError(() -> ExceptionResponseHandler.handleBindException(ex));
    }

    /**
     * Handles validation error.
     *
     * @param ex -  validation error exception
     * @return response entity
     */
    @ExceptionHandler(ValidationErrorException.class)
    public ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleValidationError(ValidationErrorException ex) {
        log.error("Validation error [{}]: {}", ex.getErrorCode(), ex.getMessage());
        var responseEntity = ExceptionResponseHandler.handleValidationErrorException(ex);
        var responseDto = buildResponse(ResponseCode.VALIDATION_ERROR, responseEntity.getBody());
        return ResponseEntity.badRequest().body(responseDto);
    }

    private ResponseEntity<ResponseDto<List<ValidationErrorDto>>> handleValidationError(
            Supplier<ResponseEntity<List<ValidationErrorDto>>> supplier) {
        metricsService.trackRequestsTotal();
        ResponseEntity<List<ValidationErrorDto>> errorResponse = supplier.get();
        ResponseDto<List<ValidationErrorDto>> responseDto =
                buildResponse(ResponseCode.VALIDATION_ERROR, errorResponse.getBody());
        log.error("Validation error: {}", responseDto);
        metricsService.trackResponseCode(responseDto.getResponseCode());
        metricsService.trackResponsesTotal();
        return ResponseEntity.badRequest().body(responseDto);
    }
}
