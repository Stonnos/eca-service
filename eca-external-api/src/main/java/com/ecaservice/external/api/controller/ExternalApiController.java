package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.service.EcaRequestService;
import com.ecaservice.external.api.service.EvaluationApiService;
import com.ecaservice.external.api.service.InstancesService;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.validation.annotations.ValidTrainData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
import java.util.Optional;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.external.api.controller.docs.ApiExamples.EVALUATION_REQUEST_JSON;
import static com.ecaservice.external.api.util.Constants.DATA_URL_PREFIX;
import static com.ecaservice.external.api.util.Utils.buildAttachmentResponse;
import static com.ecaservice.external.api.util.Utils.buildResponse;
import static com.ecaservice.external.api.util.Utils.existsFile;
import static com.ecaservice.external.api.util.Utils.toJson;

/**
 * External API controller.
 *
 * @author Roman Batygin
 */
@Tag(name = "Operations for external API")
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ExternalApiController {

    private final ExternalApiConfig externalApiConfig;
    private final MessageCorrelationService messageCorrelationService;
    private final EvaluationApiService evaluationApiService;
    private final EcaRequestService ecaRequestService;
    private final TimeoutFallback timeoutFallback;
    private final InstancesService instancesService;

    /**
     * Uploads train data file.
     *
     * @param trainingData - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @return instances dto
     * @throws IOException in case of I/O error
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @Operation(
            description = "Uploads train data file",
            summary = "Uploads train data file",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @PostMapping(value = "/uploads-train-data")
    public ResponseDto<InstancesDto> uploadInstances(
            @Parameter(description = "Training data file", required = true)
            @ValidTrainData
            @RequestParam MultipartFile trainingData) throws IOException {
        log.debug("Received request to upload train data [{}]", trainingData.getOriginalFilename());
        var instancesEntity = instancesService.uploadInstances(trainingData);
        var instancesDto = new InstancesDto(instancesEntity.getUuid(),
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
    @Operation(
            description = "Processes evaluation request",
            summary = "Processes evaluation request",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = EVALUATION_REQUEST_JSON)
                    })
            })
    )
    @PostMapping(value = "/evaluate")
    public Mono<ResponseDto<EvaluationResponseDto>> evaluateModel(
            @Valid @RequestBody EvaluationRequestDto evaluationRequestDto) {
        if (log.isDebugEnabled()) {
            log.debug("Received request with options [{}], evaluation method [{}]",
                    toJson(evaluationRequestDto.getClassifierOptions()), evaluationRequestDto.getEvaluationMethod());
        }
        var ecaRequestEntity = ecaRequestService.createAndSaveRequestEntity(evaluationRequestDto);
        return Mono.<ResponseDto<EvaluationResponseDto>>create(sink -> {
            messageCorrelationService.push(ecaRequestEntity.getCorrelationId(), sink);
            evaluationApiService.processRequest(ecaRequestEntity, evaluationRequestDto);
        }).timeout(Duration.ofMinutes(externalApiConfig.getRequestTimeoutMinutes()),
                timeoutFallback.timeout(ecaRequestEntity.getCorrelationId()));
    }

    /**
     * Downloads classifier model.
     *
     * @param requestId - request id
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @Operation(
            description = "Downloads classifier model",
            summary = "Downloads classifier model",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/download-model/{requestId}")
    public ResponseEntity<FileSystemResource> downloadModel(@Parameter(description = "Request id", required = true)
                                                            @PathVariable String requestId) {
        var evaluationRequestEntity = ecaRequestService.getByCorrelationId(requestId);
        var modelFile = Optional.ofNullable(evaluationRequestEntity.getClassifierAbsolutePath())
                .map(File::new)
                .orElse(null);
        if (!existsFile(modelFile)) {
            log.error("Classifier model file not found for request id [{}]", requestId);
            return ResponseEntity.badRequest().build();
        }
        log.debug("Downloads classifier model file {} for request id [{}]",
                evaluationRequestEntity.getClassifierAbsolutePath(), evaluationRequestEntity.getCorrelationId());
        return buildAttachmentResponse(modelFile);
    }
}
