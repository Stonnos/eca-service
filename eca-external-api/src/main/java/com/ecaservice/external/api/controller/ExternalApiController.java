package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapper;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.service.EvaluationApiService;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.service.RequestStageHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.external.api.util.Utils.toJson;

/**
 * External API controller.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for external API")
@Slf4j
@RestController
@RequestMapping("/external")
@RequiredArgsConstructor
public class ExternalApiController {

    private final ExternalApiConfig externalApiConfig;
    private final MessageCorrelationService messageCorrelationService;
    private final EvaluationApiService evaluationApiService;
    private final EcaRequestMapper ecaRequestMapper;
    private final RequestStageHandler requestStageHandler;
    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Processes evaluation request.
     *
     * @param evaluationRequestDto - evaluation request dto.
     * @return evaluation response mono object
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @ApiOperation(
            value = "Processes evaluation request",
            notes = "Processes evaluation request"
    )
    @PostMapping(value = "/evaluate")
    public Mono<EvaluationResponseDto> evaluateModel(@Valid @RequestBody EvaluationRequestDto evaluationRequestDto) {
        log.debug("Received request with options [{}], evaluation method [{}]",
                toJson(evaluationRequestDto.getClassifierOptions()), evaluationRequestDto.getEvaluationMethod());
        EcaRequestEntity ecaRequestEntity = createAndSaveRequestEntity(evaluationRequestDto);
        return Mono.<EvaluationResponseDto>create(sink -> {
            messageCorrelationService.push(ecaRequestEntity.getCorrelationId(), sink);
            evaluationApiService.processRequest(ecaRequestEntity, evaluationRequestDto);
        }).timeout(Duration.ofMinutes(externalApiConfig.getRequestTimeoutMinutes()), Mono.create(timeoutSink -> {
            requestStageHandler.handleExceeded(ecaRequestEntity.getCorrelationId());
            EvaluationResponseDto evaluationResponseDto = EvaluationResponseDto.builder()
                    .requestId(ecaRequestEntity.getCorrelationId())
                    .status(RequestStatus.TIMEOUT)
                    .build();
            log.debug("Send response with timeout for correlation id [{}]", ecaRequestEntity.getCorrelationId());
            timeoutSink.success(evaluationResponseDto);
        }));
    }

    private EcaRequestEntity createAndSaveRequestEntity(EvaluationRequestDto evaluationRequestDto) {
        EvaluationRequestEntity ecaRequestEntity = ecaRequestMapper.map(evaluationRequestDto);
        ecaRequestEntity.setCorrelationId(UUID.randomUUID().toString());
        ecaRequestEntity.setRequestStage(RequestStageType.NOT_SEND);
        ecaRequestEntity.setClassifierOptionsJson(toJson(evaluationRequestDto.getClassifierOptions()));
        ecaRequestEntity.setCreationDate(LocalDateTime.now());
        return ecaRequestRepository.save(ecaRequestEntity);
    }
}
