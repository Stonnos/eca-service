package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import com.ecaservice.external.api.dto.EvaluationResultsResponsePayloadDto;
import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.InstancesRequestDto;
import com.ecaservice.external.api.dto.InstancesResponseDto;
import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import com.ecaservice.external.api.dto.SimpleEvaluationResponsePayloadDto;
import com.ecaservice.external.api.dto.ValidationErrorResponsePayloadDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.service.EcaRequestService;
import com.ecaservice.external.api.service.EvaluationApiService;
import com.ecaservice.external.api.service.EvaluationResponseService;
import com.ecaservice.external.api.service.InstancesService;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.external.api.validation.annotations.ValidTrainData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
import javax.validation.constraints.Size;
import java.io.IOException;
import java.time.Duration;
import java.util.function.BiConsumer;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.external.api.dto.Constraints.MIN_LENGTH_1;
import static com.ecaservice.external.api.util.Constants.DATA_URL_PREFIX;
import static com.ecaservice.external.api.util.Utils.buildResponse;
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

    private static final String SCOPE_EXTERNAL_API = "external-api";

    private final ExternalApiConfig externalApiConfig;
    private final MessageCorrelationService messageCorrelationService;
    private final EvaluationApiService evaluationApiService;
    private final EcaRequestService ecaRequestService;
    private final TimeoutFallback timeoutFallback;
    private final InstancesService instancesService;
    private final EvaluationResponseService evaluationResponseService;

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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_EXTERNAL_API),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UploadTrainDataResponse",
                                                    ref = "#/components/examples/UploadTrainDataResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = InstancesResponseDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "UploadTrainDataBadRequestResponse",
                                                    ref = "#/components/examples/UploadTrainDataBadRequestResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ValidationErrorResponsePayloadDto.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/uploads-train-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseDto<InstancesDto> uploadInstances(
            @Parameter(description = "Training data file", required = true)
            @ValidTrainData
            @RequestParam MultipartFile trainingData) throws IOException {
        log.info("Received request to upload train data [{}]", trainingData.getOriginalFilename());
        var instancesEntity = instancesService.uploadInstances(trainingData);
        var instancesDto = new InstancesDto(instancesEntity.getUuid(),
                String.format("%s%s", DATA_URL_PREFIX, instancesEntity.getUuid()));
        return buildResponse(ResponseCode.SUCCESS, instancesDto);
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_EXTERNAL_API),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "EvaluationRequest",
                                    ref = "#/components/examples/EvaluationRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationResponse",
                                                    ref = "#/components/examples/EvaluationResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = EvaluationResultsResponsePayloadDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationBadRequestResponse",
                                                    ref = "#/components/examples/EvaluationBadRequestResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ValidationErrorResponsePayloadDto.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/evaluation-request")
    public Mono<ResponseDto<SimpleEvaluationResponseDto>> evaluateModel(
            @Valid @RequestBody EvaluationRequestDto evaluationRequestDto) {
        if (log.isDebugEnabled()) {
            log.debug("Received request with options [{}], evaluation method [{}]",
                    toJson(evaluationRequestDto.getClassifierOptions()), evaluationRequestDto.getEvaluationMethod());
        }
        var ecaRequestEntity = ecaRequestService.createAndSaveEvaluationRequestEntity(evaluationRequestDto);
        return evaluateModel(evaluationApiService::processRequest, ecaRequestEntity, evaluationRequestDto);
    }

    /**
     * Processes evaluation request using optimal classifier model.
     *
     * @param instancesRequestDto - instances request dto
     * @return evaluation response mono object
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @Operation(
            description = "Processes evaluation request using optimal classifier model",
            summary = "Processes evaluation request using optimal classifier model",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_EXTERNAL_API),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "OptimalEvaluationRequest",
                                    ref = "#/components/examples/OptimalEvaluationRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationResponse",
                                                    ref = "#/components/examples/EvaluationResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = EvaluationResultsResponsePayloadDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "OptimalEvaluationBadRequestResponse",
                                                    ref = "#/components/examples/OptimalEvaluationBadRequestResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ValidationErrorResponsePayloadDto.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/optimal-evaluation-request")
    public Mono<ResponseDto<SimpleEvaluationResponseDto>> evaluateOptimalModel(
            @Valid @RequestBody InstancesRequestDto instancesRequestDto) {
        log.info("Received request to evaluate optimal classifier for data url [{}]",
                instancesRequestDto.getTrainDataUrl());
        var ecaRequestEntity = ecaRequestService.createAndSaveEvaluationOptimizerRequestEntity();
        return evaluateModel(evaluationApiService::processRequest, ecaRequestEntity, instancesRequestDto);
    }

    /**
     * Creates experiment request.
     *
     * @param experimentRequestDto - evaluation request dto.
     * @return evaluation response mono object
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @Operation(
            description = "Creates experiment request",
            summary = "Creates experiment request",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_EXTERNAL_API),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "ExperimentRequest",
                                    ref = "#/components/examples/ExperimentRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "SimpleEvaluationResponse",
                                                    ref = "#/components/examples/SimpleEvaluationResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = SimpleEvaluationResponsePayloadDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ExperimentBadRequestResponse",
                                                    ref = "#/components/examples/ExperimentBadRequestResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ValidationErrorResponsePayloadDto.class)
                            )
                    )
            }
    )
    @PostMapping(value = "/experiment-request")
    public Mono<ResponseDto<SimpleEvaluationResponseDto>> createExperimentRequest(
            @Valid @RequestBody ExperimentRequestDto experimentRequestDto) {
        log.info("Received experiment request [{}], evaluation method [{}]", experimentRequestDto.getExperimentType(),
                experimentRequestDto.getEvaluationMethod());
        var ecaRequestEntity = ecaRequestService.createAndSaveExperimentRequestEntity(experimentRequestDto);
        return evaluateModel(evaluationApiService::processRequest, ecaRequestEntity, experimentRequestDto);
    }

    /**
     * Gets evaluation response status.
     *
     * @param requestId - request id
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @Operation(
            description = "Gets evaluation response status",
            summary = "Gets evaluation response status",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_EXTERNAL_API),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationStatusResponse",
                                                    ref = "#/components/examples/EvaluationStatusResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = EvaluationResultsResponsePayloadDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            ),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ValidationErrorResponsePayloadDto.class)
                            )
                    )
            }
    )
    @GetMapping(value = "/evaluation-status/{requestId}")
    public ResponseDto<EvaluationResultsResponseDto> getEvaluationResponseStatus(
            @Parameter(description = "Request id", required = true)
            @Size(min = MIN_LENGTH_1, max = MAX_LENGTH_255) @PathVariable String requestId) {
        log.debug("Request to get evaluation [{}] response status", requestId);
        var evaluationResponseDto = evaluationResponseService.processResponse(requestId);
        var responseDto = buildResponse(ResponseCode.SUCCESS, evaluationResponseDto);
        log.debug("Got evaluation [{}] response: {}", requestId, responseDto);
        return responseDto;
    }

    private <T> Mono<ResponseDto<SimpleEvaluationResponseDto>> evaluateModel(
            BiConsumer<EcaRequestEntity, T> requestConsumer,
            EcaRequestEntity ecaRequestEntity,
            T requestDto) {
        return Mono.<ResponseDto<SimpleEvaluationResponseDto>>create(sink -> {
            messageCorrelationService.push(ecaRequestEntity.getCorrelationId(), sink);
            requestConsumer.accept(ecaRequestEntity, requestDto);

        }).timeout(Duration.ofSeconds(externalApiConfig.getRequestTimeoutSeconds()),
                timeoutFallback.timeout(ecaRequestEntity.getCorrelationId()));
    }
}
