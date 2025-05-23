package com.ecaservice.server.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.dto.CreateEvaluationRequestDto;
import com.ecaservice.server.dto.CreateOptimalEvaluationRequestDto;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.evaluation.ClassifyEvaluationInstanceService;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import com.ecaservice.server.service.evaluation.EvaluationRequestWebApiService;
import com.ecaservice.server.service.evaluation.EvaluationRocCurveDataProvider;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.ClassifyInstanceRequestDto;
import com.ecaservice.web.dto.model.ClassifyInstanceResultDto;
import com.ecaservice.web.dto.model.CreateEvaluationResponseDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.EvaluationLogsPageDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.RocCurveDataDto;
import com.ecaservice.web.dto.model.S3ContentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_0;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Classifiers evaluation API for web application.
 *
 * @author Roman Batygin
 */
@Tag(name = "Classifiers evaluation API for web application")
@Slf4j
@Validated
@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationLogDataService evaluationLogDataService;
    private final EvaluationRequestWebApiService evaluationRequestWebApiService;
    private final EvaluationRocCurveDataProvider evaluationRocCurveDataProvider;
    private final ClassifyEvaluationInstanceService classifyEvaluationInstanceService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Creates classifier evaluation request with specified options.
     *
     * @param evaluationRequestDto - evaluation request dto
     * @return evaluation response dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Creates classifier evaluation request with specified options",
            summary = "Creates classifier evaluation request with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "CreateEvaluationRequest",
                                    ref = "#/components/examples/CreateEvaluationRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "CreateEvaluationResponse",
                                                    ref = "#/components/examples/CreateEvaluationResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = CreateEvaluationResponseDto.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "CreateEvaluationBadRequestResponse",
                                                    ref = "#/components/examples/CreateEvaluationBadRequestResponse"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/create")
    public CreateEvaluationResponseDto createRequest(
            @Valid @RequestBody CreateEvaluationRequestDto evaluationRequestDto) {
        log.info("Received evaluation request [{}]", evaluationRequestDto);
        return evaluationRequestWebApiService.createEvaluationRequest(evaluationRequestDto);
    }

    /**
     * Creates classifier evaluation request with optimal classifiers options.
     *
     * @param evaluationRequestDto - optimal evaluation request dto
     * @return evaluation response dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Creates classifier evaluation request with optimal classifiers options",
            summary = "Creates classifier evaluation request with optimal classifiers options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "CreateOptimalEvaluationRequest",
                                    ref = "#/components/examples/CreateOptimalEvaluationRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "CreateEvaluationResponse",
                                                    ref = "#/components/examples/CreateEvaluationResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = CreateEvaluationResponseDto.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "CreateEvaluationBadRequestResponse",
                                                    ref = "#/components/examples/CreateEvaluationBadRequestResponse"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/create-optimal")
    public CreateEvaluationResponseDto createRequest(
            @Valid @RequestBody CreateOptimalEvaluationRequestDto evaluationRequestDto) {
        log.info("Received optimal evaluation request [{}]", evaluationRequestDto);
        return evaluationRequestWebApiService.createOptimalEvaluationRequest(evaluationRequestDto);
    }

    /**
     * Finds evaluation logs with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return evaluations logs page
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Finds evaluation logs with specified options",
            summary = "Finds evaluation logs with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "EvaluationLogsPageRequest",
                                    ref = "#/components/examples/EvaluationLogsPageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationLogsPageResponse",
                                                    ref = "#/components/examples/EvaluationLogsPageResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = EvaluationLogsPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "BadPageRequestResponse",
                                                    ref = "#/components/examples/BadPageRequestResponse"
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/list")
    public PageDto<EvaluationLogDto> getEvaluationLogs(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received evaluation logs page request: {}", pageRequestDto);
        return evaluationLogDataService.getEvaluationLogsPage(pageRequestDto);
    }

    /**
     * Gets evaluation log details.
     *
     * @param id - evaluation log id
     * @return evaluation log details report
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets evaluation log details",
            summary = "Gets evaluation log details",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "EvaluationLogDetailsResponse",
                                                    ref = "#/components/examples/EvaluationLogDetailsResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = EvaluationLogDetailsDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
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
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/details/{id}")
    public EvaluationLogDetailsDto getEvaluationLogDetails(
            @Parameter(description = "Evaluation log id", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        log.info("Received request for evaluation log details for id [{}]", id);
        EvaluationLog evaluationLog = evaluationLogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EvaluationLog.class, id));
        return evaluationLogDataService.getEvaluationLogDetails(evaluationLog);
    }

    /**
     * Gets evaluations request statuses statistics.
     *
     * @return evaluations request statuses statistics dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets evaluations request statuses statistics",
            summary = "Gets evaluations request statuses statistics",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "RequestStatusStatisticsResponse",
                                                    ref = "#/components/examples/RequestStatusStatisticsResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = RequestStatusStatisticsDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/request-statuses-statistics")
    public RequestStatusStatisticsDto getEvaluationRequestStatusesStatistics() {
        return evaluationLogDataService.getRequestStatusesStatistics();
    }

    /**
     * Gets classifiers statistics data (distribution diagram by classifier).
     *
     * @param createdDateFrom - created date from
     * @param createdDateTo   - created date to
     * @return classifiers statistics data
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets classifiers statistics data (distribution diagram by classifier)",
            summary = "Gets classifiers statistics data (distribution diagram by classifier)",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ClassifiersStatisticsResponse",
                                                    ref = "#/components/examples/ClassifiersStatisticsResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ChartDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/classifiers-statistics")
    public ChartDto getClassifiersStatisticsData(
            @Parameter(description = "Created date from", example = "2021-07-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateFrom,
            @Parameter(description = "Created date to", example = "2021-07-10")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateTo) {
        log.info("Received request to get classifiers statistics histogram data with created date from [{}] to [{}]",
                createdDateFrom, createdDateTo);
        return evaluationLogDataService.getClassifiersStatisticsData(createdDateFrom, createdDateTo);
    }

    /**
     * Gets classifier model content url.
     *
     * @param id - evaluation id
     * @return s3 content response dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets classifier model content url",
            summary = "Gets classifier model content url",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "GetClassifierModelContentResponse",
                                                    ref = "#/components/examples/GetClassifierModelContentResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = S3ContentResponseDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
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
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/model/{id}")
    public S3ContentResponseDto getModelContentUrl(
            @Parameter(description = "Evaluation id", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        log.info("Received request to get classifier model [{}] result content url", id);
        return evaluationLogDataService.getModelContentUrl(id);
    }

    /**
     * Gets evaluation roc curve data.
     *
     * @param evaluationLogId - evaluation log id
     * @param classValueIndex - class value index
     * @return roc curve data
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets evaluation roc curve data",
            summary = "Gets evaluation roc curve data",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "RocCurveDataResponse",
                                                    ref = "#/components/examples/RocCurveDataResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = RocCurveDataDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
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
                                            )
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping("/roc-curve")
    public RocCurveDataDto getRocCurveData(@Parameter(description = "Evaluation log id", example = "1", required = true)
                                           @Min(VALUE_1) @Max(Long.MAX_VALUE)
                                           @RequestParam Long evaluationLogId,
                                           @Parameter(description = "Class value index", example = "1", required = true)
                                           @Min(VALUE_0) @Max(Long.MAX_VALUE)
                                           @RequestParam Integer classValueIndex) {
        log.info("Request to calculate roc curve data for evaluation log [{}], class index [{}]", evaluationLogId,
                classValueIndex);
        return evaluationRocCurveDataProvider.getRocCurveData(evaluationLogId, classValueIndex);
    }

    /**
     * Classify instance for specified classifier model.
     *
     * @param classifyInstanceRequestDto - classify instance request dto
     * @return classify instance result
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Classify instance for specified classifier model",
            summary = "Classify instance for specified classifier model",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "ClassifyInstanceRequest",
                                    ref = "#/components/examples/ClassifyInstanceRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ClassifyInstanceResponse",
                                                    ref = "#/components/examples/ClassifyInstanceResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ClassifyInstanceResultDto.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "DataNotFoundResponse",
                                                    ref = "#/components/examples/DataNotFoundResponse"
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "NotAuthorizedResponse",
                                                    ref = "#/components/examples/NotAuthorizedResponse"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/classify-instance")
    public ClassifyInstanceResultDto classifyInstance(
            @Valid @RequestBody ClassifyInstanceRequestDto classifyInstanceRequestDto) {
        log.info("Received classify instance request [{}]", classifyInstanceRequestDto);
        return classifyEvaluationInstanceService.classifyInstance(classifyInstanceRequestDto);
    }
}
