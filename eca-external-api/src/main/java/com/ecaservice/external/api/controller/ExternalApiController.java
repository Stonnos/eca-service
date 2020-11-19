package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.mapping.EcaRequestMapper;
import com.ecaservice.external.api.metrics.MetricsService;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.service.EvaluationApiService;
import com.ecaservice.external.api.service.InstancesService;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.service.RequestStageHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.ecaservice.external.api.util.Constants.DATA_URL_PREFIX;
import static com.ecaservice.external.api.util.Utils.buildAttachmentResponse;
import static com.ecaservice.external.api.util.Utils.buildResponse;
import static com.ecaservice.external.api.util.Utils.existsFile;
import static com.ecaservice.external.api.util.Utils.isValidTrainData;
import static com.ecaservice.external.api.util.Utils.toJson;

/**
 * External API controller.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for external API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ExternalApiController {

    private final ExternalApiConfig externalApiConfig;
    private final MessageCorrelationService messageCorrelationService;
    private final EvaluationApiService evaluationApiService;
    private final EcaRequestMapper ecaRequestMapper;
    private final RequestStageHandler requestStageHandler;
    private final InstancesService instancesService;
    private final MetricsService metricsService;
    private final EcaRequestRepository ecaRequestRepository;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Uploads train data file.
     *
     * @param trainingData - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @return instances dto
     * @throws IOException in case of I/O error
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @ApiOperation(
            value = "Uploads train data file",
            notes = "Uploads train data file"
    )
    @PostMapping(value = "/uploads-train-data")
    public ResponseDto<InstancesDto> uploadInstances(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData)
            throws IOException {
        log.debug("Received request to upload train data [{}]", trainingData.getOriginalFilename());
        if (!isValidTrainData(trainingData.getOriginalFilename())) {
            log.error("Got training data with invalid extension: [{}]", trainingData.getOriginalFilename());
            return buildResponse(RequestStatus.INVALID_TRAIN_DATA_EXTENSION);
        }
        InstancesEntity instancesEntity = instancesService.uploadInstances(trainingData);
        InstancesDto instancesDto = new InstancesDto(instancesEntity.getUuid(),
                String.format("%s%s", DATA_URL_PREFIX, instancesEntity.getUuid()));
        return buildResponse(RequestStatus.SUCCESS, instancesDto);
    }

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
    public Mono<ResponseDto<EvaluationResponseDto>> evaluateModel(
            @Valid @RequestBody EvaluationRequestDto evaluationRequestDto) {
        if (log.isDebugEnabled()) {
            log.debug("Received request with options [{}], evaluation method [{}]",
                    toJson(evaluationRequestDto.getClassifierOptions()), evaluationRequestDto.getEvaluationMethod());
        }
        EcaRequestEntity ecaRequestEntity = createAndSaveRequestEntity(evaluationRequestDto);
        return Mono.<ResponseDto<EvaluationResponseDto>>create(sink -> {
            messageCorrelationService.push(ecaRequestEntity.getCorrelationId(), sink);
            evaluationApiService.processRequest(ecaRequestEntity, evaluationRequestDto);
        }).timeout(Duration.ofMinutes(externalApiConfig.getRequestTimeoutMinutes()), Mono.create(timeoutSink -> {
            requestStageHandler.handleExceeded(ecaRequestEntity.getCorrelationId());
            EvaluationResponseDto evaluationResponseDto = EvaluationResponseDto.builder()
                    .requestId(ecaRequestEntity.getCorrelationId())
                    .build();
            ResponseDto<EvaluationResponseDto> responseDto =
                    buildResponse(RequestStatus.TIMEOUT, evaluationResponseDto);
            metricsService.trackResponse(ecaRequestEntity, responseDto.getRequestStatus());
            log.debug("Send response with timeout for correlation id [{}]", ecaRequestEntity.getCorrelationId());
            timeoutSink.success(responseDto);
        }));
    }

    /**
     * Downloads classifier model.
     *
     * @param requestId - request id
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @ApiOperation(
            value = "Downloads classifier model",
            notes = "Downloads classifier model"
    )
    @GetMapping(value = "/download-model/{requestId}")
    public ResponseEntity<FileSystemResource> downloadModel(
            @ApiParam(value = "Request id", required = true) @PathVariable String requestId) {
        EvaluationRequestEntity evaluationRequestEntity = evaluationRequestRepository.findByCorrelationId(requestId);
        if (evaluationRequestEntity == null) {
            log.error("Evaluation request with id [{}] not found", requestId);
            return ResponseEntity.notFound().build();
        }
        File modelFile =
                Optional.ofNullable(evaluationRequestEntity.getClassifierAbsolutePath()).map(File::new).orElse(null);
        if (!existsFile(modelFile)) {
            log.error("Classifier model file not found for request id [{}]", requestId);
            return ResponseEntity.notFound().build();
        }
        log.debug("Downloads classifier model file {} for request id [{}]",
                evaluationRequestEntity.getClassifierAbsolutePath(), evaluationRequestEntity.getCorrelationId());
        return buildAttachmentResponse(modelFile);
    }

    private EcaRequestEntity createAndSaveRequestEntity(EvaluationRequestDto evaluationRequestDto) {
        EvaluationRequestEntity ecaRequestEntity = ecaRequestMapper.map(evaluationRequestDto);
        ecaRequestEntity.setCorrelationId(UUID.randomUUID().toString());
        ecaRequestEntity.setRequestStage(RequestStageType.NOT_SEND);
        ecaRequestEntity.setCreationDate(LocalDateTime.now());
        return ecaRequestRepository.save(ecaRequestEntity);
    }
}
