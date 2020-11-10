package com.ecaservice.external.api.aspect;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.exception.ExceptionTranslator;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.service.RequestStageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;


/**
 * Aspect for error handling.
 *
 * @author Roman Batygin
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ErrorHandlerAspect {

    private static final int ECA_REQUEST_INDEX = 0;

    private final MessageCorrelationService messageCorrelationService;
    private final ExceptionTranslator exceptionTranslator;
    private final RequestStageHandler requestStageHandler;

    /**
     * Handles error. Send error response back to client.
     *
     * @param joinPoint      - joint point
     * @param errorExecution - error execution
     * @return any object
     */
    @Around("execution(@com.ecaservice.external.api.aspect.ErrorExecution * * (..)) && @annotation(errorExecution)")
    public Object around(ProceedingJoinPoint joinPoint, ErrorExecution errorExecution) throws Throwable {
        EcaRequestEntity ecaRequestEntity =
                getInputParameter(joinPoint.getArgs(), ECA_REQUEST_INDEX, EcaRequestEntity.class);
        try {
            log.debug("Starting to process request with correlation id [{}], options [{}], evaluationMethod [{}]",
                    ecaRequestEntity.getCorrelationId(), ecaRequestEntity.getClassifierOptionsJson(),
                    ecaRequestEntity.getEvaluationMethod());
            Object result = joinPoint.proceed();
            log.debug("Request [{}] has been sent to eca - server", ecaRequestEntity.getCorrelationId());
            return result;
        } catch (Exception ex) {
            log.error("There was an error while request processing with correlation id [{}]: {}",
                    ecaRequestEntity.getCorrelationId(), ex.getMessage(), ex);
            handleError(ecaRequestEntity, ex);
        }
        return null;
    }

    private void handleError(EcaRequestEntity ecaRequestEntity, Exception ex) {
        requestStageHandler.handleError(ecaRequestEntity.getCorrelationId(), ex);
        messageCorrelationService.pop(ecaRequestEntity.getCorrelationId()).ifPresent(sink -> {
            RequestStatus requestStatus = exceptionTranslator.translate(ex);
            EvaluationResponseDto evaluationResponseDto = EvaluationResponseDto.builder()
                    .requestId(ecaRequestEntity.getCorrelationId())
                    .status(requestStatus)
                    .build();
            log.debug("Send error response for correlation id [{}]", ecaRequestEntity.getCorrelationId());
            sink.success(evaluationResponseDto);
        });
    }

    private <T> T getInputParameter(Object[] inputArgs, int index, Class<T> clazz) {
        if (inputArgs == null || inputArgs.length == 0) {
            throw new IllegalStateException(MessageFormat.format("Empty parameters for method annotated by [{0}]",
                    ErrorExecution.class.getSimpleName()));
        }
        Object inputArg = inputArgs[index];
        if (inputArg == null) {
            throw new IllegalStateException(
                    MessageFormat.format("Got null parameter value at index [{0}] for method annotated by [{1}]", index,
                            ErrorExecution.class.getSimpleName()));
        }
        if (!clazz.isInstance(inputArg)) {
            throw new IllegalStateException(MessageFormat.format(
                    "Expected class [{0}] as parameter [{1}] for method annotated by [{2}], but got [{3}]",
                    clazz.getSimpleName(), index, ErrorExecution.class.getSimpleName(),
                    inputArg.getClass().getSimpleName()));
        }
        return clazz.cast(inputArg);
    }
}
