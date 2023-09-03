package com.ecaservice.external.api.controller;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.dto.ExperimentResultsResponseDto;
import com.ecaservice.external.api.dto.InstancesRequestDto;
import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import com.ecaservice.external.api.service.EvaluationApiService;
import com.ecaservice.external.api.service.EvaluationResultsResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.external.api.dto.Constraints.MIN_LENGTH_1;
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

    private final EvaluationApiService evaluationApiService;
    private final EvaluationResultsResponseService evaluationResultsResponseService;

    /**
     * Processes evaluation request.
     *
     * @param evaluationRequestDto - evaluation request dto.
     * @return evaluation response object
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
                                    schema = @Schema(implementation = SimpleEvaluationResponseDto.class)
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
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/evaluation-request")
    public SimpleEvaluationResponseDto evaluateModel(
            @Valid @RequestBody EvaluationRequestDto evaluationRequestDto) {
        if (log.isDebugEnabled()) {
            log.debug("Received request with options [{}], evaluation method [{}]",
                    toJson(evaluationRequestDto.getClassifierOptions()), evaluationRequestDto.getEvaluationMethod());
        }
        return evaluationApiService.processRequest(evaluationRequestDto);
    }

    /**
     * Processes evaluation request using optimal classifier model.
     *
     * @param instancesRequestDto - instances request dto
     * @return evaluation response object
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
                                    schema = @Schema(implementation = SimpleEvaluationResponseDto.class)
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
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/optimal-evaluation-request")
    public SimpleEvaluationResponseDto evaluateOptimalModel(
            @Valid @RequestBody InstancesRequestDto instancesRequestDto) {
        log.info("Received request to evaluate optimal classifier for data uuid [{}]",
                instancesRequestDto.getTrainDataUuid());
        return evaluationApiService.processRequest(instancesRequestDto);
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
                                    schema = @Schema(implementation = SimpleEvaluationResponseDto.class)
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
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/experiment-request")
    public SimpleEvaluationResponseDto createExperimentRequest(
            @Valid @RequestBody ExperimentRequestDto experimentRequestDto) {
        log.info("Received experiment request [{}], evaluation method [{}]", experimentRequestDto.getExperimentType(),
                experimentRequestDto.getEvaluationMethod());
        return evaluationApiService.processRequest(experimentRequestDto);
    }

    /**
     * Gets evaluation results response.
     *
     * @param requestId - request id
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @Operation(
            description = "Gets evaluation results response",
            summary = "Gets evaluation results response",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_EXTERNAL_API),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationResultsResponse",
                                                    ref = "#/components/examples/EvaluationResultsResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = EvaluationResultsResponseDto.class)
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
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/evaluation-results/{requestId}")
    public EvaluationResultsResponseDto getEvaluationResults(
            @Parameter(description = "Request id", required = true)
            @Size(min = MIN_LENGTH_1, max = MAX_LENGTH_255) @PathVariable String requestId) {
        log.debug("Request to get evaluation [{}] results", requestId);
        return evaluationResultsResponseService.getEvaluationResultsResponse(requestId);
    }

    /**
     * Gets experiment results response.
     *
     * @param requestId - request id
     */
    @PreAuthorize("#oauth2.hasScope('external-api')")
    @Operation(
            description = "Gets experiment results response",
            summary = "Gets evaluation results response",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_EXTERNAL_API),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ExperimentResultsResponse",
                                                    ref = "#/components/examples/ExperimentResultsResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ExperimentResultsResponseDto.class)
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
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/experiment-results/{requestId}")
    public ExperimentResultsResponseDto getExperimentResults(
            @Parameter(description = "Request id", required = true)
            @Size(min = MIN_LENGTH_1, max = MAX_LENGTH_255) @PathVariable String requestId) {
        log.debug("Request to get experiment [{}] results", requestId);
        return evaluationResultsResponseService.getExperimentResultsResponse(requestId);
    }
}
