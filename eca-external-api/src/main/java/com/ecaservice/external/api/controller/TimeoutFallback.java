package com.ecaservice.external.api.controller;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.metrics.MetricsService;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.service.RequestStageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.ecaservice.external.api.util.Utils.buildResponse;

/**
 * Timeout fallback service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TimeoutFallback {

    private final RequestStageHandler requestStageHandler;
    private final MetricsService metricsService;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles request timeout.
     *
     * @param correlationId - request correlation id
     * @return mono object
     */
    public Mono<ResponseDto<EvaluationResponseDto>> timeout(String correlationId) {
        return Mono.create(timeoutSink -> {
            var ecaRequestEntity = getByCorrelationId(correlationId);
            requestStageHandler.handleExceeded(ecaRequestEntity);
            var evaluationResponseDto = EvaluationResponseDto.builder()
                    .requestId(ecaRequestEntity.getCorrelationId())
                    .build();
            var responseDto = buildResponse(RequestStatus.TIMEOUT, evaluationResponseDto);
            metricsService.trackResponse(ecaRequestEntity, responseDto.getRequestStatus());
            log.debug("Send response with timeout for correlation id [{}]", ecaRequestEntity.getCorrelationId());
            timeoutSink.success(responseDto);
        });
    }

    private EcaRequestEntity getByCorrelationId(String correlationId) {
        return ecaRequestRepository.findByCorrelationId(correlationId)
                .orElseThrow(() -> new EntityNotFoundException(EcaRequestEntity.class, correlationId));
    }
}
