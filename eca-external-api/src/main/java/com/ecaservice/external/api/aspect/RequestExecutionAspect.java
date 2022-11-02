package com.ecaservice.external.api.aspect;

import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.error.ExceptionTranslator;
import com.ecaservice.external.api.metrics.MetricsService;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.service.RequestStageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

import static com.ecaservice.external.api.util.Utils.buildResponse;


/**
 * Aspect for requests handling.
 *
 * @author Roman Batygin
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RequestExecutionAspect {

    private static final int ECA_REQUEST_INDEX = 0;

    private final MessageCorrelationService messageCorrelationService;
    private final MetricsService metricsService;
    private final ExceptionTranslator exceptionTranslator;
    private final RequestStageHandler requestStageHandler;

    /**
     * Handles request. Send error response back to client if occurs.
     *
     * @param joinPoint        - joint point
     * @param requestExecution - error execution
     * @return any object
     */
    @Around("execution(@com.ecaservice.external.api.aspect.RequestExecution * * (..)) && @annotation(requestExecution)")
    public Object around(ProceedingJoinPoint joinPoint, RequestExecution requestExecution) throws Throwable {
        metricsService.trackRequestsTotal();
        var ecaRequestEntity = getInputParameter(joinPoint.getArgs(), ECA_REQUEST_INDEX, EcaRequestEntity.class);
        try {
            log.debug("Around: starting to process request with correlation id [{}]",
                    ecaRequestEntity.getCorrelationId());
            Object result = joinPoint.proceed();
            log.debug("Around: request [{}] has been processed", ecaRequestEntity.getCorrelationId());
            handleSuccess(ecaRequestEntity);
            return result;
        } catch (Exception ex) {
            log.error("There was an error while request processing with correlation id [{}]: {}",
                    ecaRequestEntity.getCorrelationId(), ex.getMessage(), ex);
            handleError(ecaRequestEntity, ex);
        }
        return null;
    }

    private void handleSuccess(EcaRequestEntity ecaRequestEntity) {
        messageCorrelationService.pop(ecaRequestEntity.getCorrelationId()).ifPresent(sink -> {
            var evaluationResponseDto = SimpleEvaluationResponseDto.builder()
                    .requestId(ecaRequestEntity.getCorrelationId())
                    .evaluationStatus(EvaluationStatus.IN_PROGRESS)
                    .build();
            var responseDto = buildResponse(ResponseCode.SUCCESS, evaluationResponseDto);
            metricsService.trackResponse(ecaRequestEntity, responseDto.getResponseCode());
            log.info("Send response back for correlation id [{}]", ecaRequestEntity.getCorrelationId());
            sink.success(responseDto);
        });
    }

    private void handleError(EcaRequestEntity ecaRequestEntity, Exception ex) {
        requestStageHandler.handleError(ecaRequestEntity, ex.getMessage());
        messageCorrelationService.pop(ecaRequestEntity.getCorrelationId()).ifPresent(sink -> {
            var responseCode = exceptionTranslator.translate(ex);
            var evaluationResponseDto = SimpleEvaluationResponseDto.builder()
                    .requestId(ecaRequestEntity.getCorrelationId())
                    .evaluationStatus(EvaluationStatus.ERROR)
                    .errorCode(ecaRequestEntity.getErrorCode())
                    .build();
            var responseDto = buildResponse(responseCode, evaluationResponseDto);
            metricsService.trackResponse(ecaRequestEntity, responseCode);
            log.info("Send error response for correlation id [{}]", ecaRequestEntity.getCorrelationId());
            sink.success(responseDto);
        });
    }

    private <T> T getInputParameter(Object[] inputArgs, int index, Class<T> clazz) {
        if (inputArgs == null || inputArgs.length == 0) {
            throw new IllegalStateException(MessageFormat.format("Empty parameters for method annotated by [{0}]",
                    RequestExecution.class.getSimpleName()));
        }
        Object inputArg = inputArgs[index];
        if (inputArg == null) {
            throw new IllegalStateException(
                    MessageFormat.format("Got null parameter value at index [{0}] for method annotated by [{1}]", index,
                            RequestExecution.class.getSimpleName()));
        }
        if (!clazz.isInstance(inputArg)) {
            throw new IllegalStateException(MessageFormat.format(
                    "Expected class [{0}] as parameter [{1}] for method annotated by [{2}], but got [{3}]",
                    clazz.getSimpleName(), index, RequestExecution.class.getSimpleName(),
                    inputArg.getClass().getSimpleName()));
        }
        return clazz.cast(inputArg);
    }
}
