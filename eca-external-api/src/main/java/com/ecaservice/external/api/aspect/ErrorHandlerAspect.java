package com.ecaservice.external.api.aspect;

import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.exception.EntityNotFoundException;
import com.ecaservice.external.api.exception.ExceptionTranslator;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.service.MessageCorrelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;


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

    private static final int CORRELATION_ID_INDEX = 0;

    private final MessageCorrelationService messageCorrelationService;
    private final ExceptionTranslator exceptionTranslator;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles error. Send error response back to client.
     *
     * @param joinPoint      - joint point
     * @param errorExecution - error execution
     * @return any object
     */
    @Around("execution(@com.ecaservice.external.api.aspect.ErrorExecution * * (..)) && @annotation(errorExecution)")
    public Object around(ProceedingJoinPoint joinPoint, ErrorExecution errorExecution) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            handleError(joinPoint, ex);
        }
        return null;
    }

    private void handleError(ProceedingJoinPoint joinPoint, Exception ex) {
        String correlationId = getInputParameter(joinPoint.getArgs(), CORRELATION_ID_INDEX, String.class);
        EcaRequestEntity ecaRequestEntity = updateRequestEntity(correlationId, ex.getMessage());
        messageCorrelationService.pop(correlationId).ifPresent(sink -> {
            RequestStatus requestStatus = exceptionTranslator.translate(ex);
            EvaluationResponseDto evaluationResponseDto = EvaluationResponseDto.builder()
                    .requestId(ecaRequestEntity.getRequestId())
                    .status(requestStatus)
                    .build();
            sink.success(evaluationResponseDto);
        });
    }

    private EcaRequestEntity updateRequestEntity(String correlationId, String errorMessage) {
        EcaRequestEntity ecaRequestEntity = ecaRequestRepository.findByCorrelationId(correlationId).orElseThrow(
                () -> new EntityNotFoundException(EcaRequestEntity.class, correlationId));
        ecaRequestEntity.setRequestId(UUID.randomUUID().toString());
        ecaRequestEntity.setRequestStage(RequestStageType.ERROR);
        ecaRequestEntity.setErrorMessage(errorMessage);
        ecaRequestEntity.setEndDate(LocalDateTime.now());
        return ecaRequestRepository.save(ecaRequestEntity);
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
