package com.ecaservice.server.mq.listener.support;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.base.model.MessageError;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecaservice.server.util.Utils.buildValidationError;

/**
 * Validation error exception handler.
 *
 * @author Roman Batygin
 */
@Component
@ConditionalOnProperty(value = "rabbit.enabled", havingValue = "true")
public class ValidationErrorTranslator extends AbstractExceptionTranslator<MethodArgumentNotValidException> {

    /**
     * Default constructor.
     */
    public ValidationErrorTranslator() {
        super(MethodArgumentNotValidException.class);
    }

    @Override
    public EcaResponse translate(MethodArgumentNotValidException exception) {
        List<ObjectError> objectErrors = Optional.ofNullable(exception.getBindingResult())
                .map(BindingResult::getAllErrors)
                .orElse(Collections.emptyList());
        List<MessageError> errors = objectErrors
                .stream()
                .map(FieldError.class::cast)
                .map(fieldError -> {
                    String errorMessage = String.format("%s: %s", fieldError.getField(),
                            fieldError.getDefaultMessage());
                    return new MessageError(ErrorCode.INVALID_FIELD_VALUE, fieldError.getField(), errorMessage);
                })
                .collect(Collectors.toList());
        return buildValidationError(errors);
    }
}
