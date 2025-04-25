package com.ecaservice.server.controller.web;

import com.ecaservice.common.error.model.ValidationErrorDto;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.dto.CreateExperimentRequestDto;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.mapping.ExperimentProgressMapper;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.service.experiment.ClassifyExperimentResultsInstanceService;
import com.ecaservice.server.service.experiment.ExperimentDataService;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import com.ecaservice.server.service.experiment.ExperimentRequestWebApiService;
import com.ecaservice.server.service.experiment.ExperimentResultsRocCurveDataProvider;
import com.ecaservice.server.service.experiment.ExperimentResultsService;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.ClassifyInstanceRequestDto;
import com.ecaservice.web.dto.model.ClassifyInstanceResultDto;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentProgressDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.ExperimentsPageDto;
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
 * Experiments API for web application.
 *
 * @author Roman Batygin
 */
@Tag(name = "Experiments API for web application")
@Slf4j
@Validated
@RestController
@RequestMapping("/experiment")
@RequiredArgsConstructor
public class ExperimentController {

    private final ExperimentDataService experimentDataService;
    private final ExperimentResultsService experimentResultsService;
    private final ExperimentMapper experimentMapper;
    private final ExperimentProgressMapper experimentProgressMapper;
    private final ExperimentProgressService experimentProgressService;
    private final ExperimentRequestWebApiService experimentRequestWebApiService;
    private final ExperimentResultsRocCurveDataProvider experimentResultsRocCurveDataProvider;
    private final ClassifyExperimentResultsInstanceService classifyExperimentResultsInstanceService;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    /**
     * Creates experiment request.
     *
     * @param experimentRequestDto - experiment request dto
     * @return create experiment results dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Creates experiment request with specified options",
            summary = "Creates experiment request with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "CreateExperimentRequest",
                                    ref = "#/components/examples/CreateExperimentRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "CreateExperimentResponse",
                                                    ref = "#/components/examples/CreateExperimentResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = CreateExperimentResultDto.class)
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "CreateExperimentBadRequestResponse",
                                                    ref = "#/components/examples/CreateExperimentBadRequestResponse"
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
    public CreateExperimentResultDto createRequest(
            @Valid @RequestBody CreateExperimentRequestDto experimentRequestDto) {
        log.info("Received experiment request [{}]", experimentRequestDto);
        return experimentRequestWebApiService.createExperiment(experimentRequestDto);
    }

    /**
     * Cancels experiment request.
     *
     * @param id - experiment request dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Cancels experiment request",
            summary = "Cancels experiment request",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
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
    @PostMapping(value = "/cancel")
    public void cancelRequest(@Parameter(description = "Experiment id", example = "1", required = true)
                              @Min(VALUE_1) @Max(Long.MAX_VALUE)
                              @RequestParam Long id) {
        log.info("Received experiment cancel request [{}]", id);
        Experiment experiment = experimentDataService.getById(id);
        experimentRequestWebApiService.cancelExperiment(experiment);
    }

    /**
     * Finds experiments with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return experiments page dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Finds experiments with specified options",
            summary = "Finds experiments with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(
                                    name = "ExperimentsPageRequest",
                                    ref = "#/components/examples/ExperimentsPageRequest"
                            )
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ExperimentsPageResponse",
                                                    ref = "#/components/examples/ExperimentsPageResponse"
                                            ),
                                    },
                                    schema = @Schema(implementation = ExperimentsPageDto.class)
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
    public PageDto<ExperimentDto> getExperiments(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received experiments page request: {}", pageRequestDto);
        return experimentDataService.getExperimentsPage(pageRequestDto);
    }

    /**
     * Finds experiment with specified id.
     *
     * @param id - experiment id
     * @return experiment dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Finds experiment with specified id",
            summary = "Finds experiment with specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ExperimentDetailsResponse",
                                                    ref = "#/components/examples/ExperimentDetailsResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ExperimentDto.class)
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
    public ExperimentDto getExperiment(
            @Parameter(description = "Experiment id", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        log.info("Received request to get experiment details for id [{}]", id);
        Experiment experiment = experimentDataService.getById(id);
        return experimentMapper.map(experiment);
    }

    /**
     * Finds experiment results details with specified id.
     *
     * @param id - experiment results id
     * @return experiment results details dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Finds experiment results details with specified id",
            summary = "Finds experiment results details with specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ExperimentResultsDetailsResponse",
                                                    ref = "#/components/examples/ExperimentResultsDetailsResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ExperimentResultsDetailsDto.class)
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
    @GetMapping(value = "/results/details/{id}")
    public ExperimentResultsDetailsDto getExperimentResultsDetails(
            @Parameter(description = "Experiment results id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        log.info("Received request to get experiment results details for id [{}]", id);
        var experimentResultsEntity = experimentResultsEntityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentResultsEntity.class, id));
        return experimentResultsService.getExperimentResultsDetails(experimentResultsEntity);
    }

    /**
     * Gets experiments request statuses statistics.
     *
     * @return experiments request statuses statistics dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets experiments request statuses statistics",
            summary = "Gets experiments request statuses statistics",
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
    public RequestStatusStatisticsDto getExperimentsRequestStatusesStatistics() {
        return experimentDataService.getRequestStatusesStatistics();
    }

    /**
     * Gets ERS report for specified experiment.
     *
     * @param id - experiment id
     * @return ers report dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets experiment ERS report",
            summary = "Gets experiment ERS report",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ExperimentErsReportResponse",
                                                    ref = "#/components/examples/ExperimentErsReportResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ExperimentErsReportDto.class)
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
    @GetMapping(value = "/ers-report/{id}")
    public ExperimentErsReportDto getExperimentErsReport(
            @Parameter(description = "Experiment id", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        log.info("Received request for ERS report for experiment [{}]", id);
        Experiment experiment = experimentDataService.getById(id);
        return experimentResultsService.getErsReport(experiment);
    }

    /**
     * Gets experiments statistics data (distribution diagram by experiment type).
     *
     * @param createdDateFrom - experiment created date from
     * @param createdDateTo   - experiment created date to
     * @return chart data dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets experiments statistics data (distribution diagram by experiment type)",
            summary = "Gets experiments statistics data (distribution diagram by experiment type)",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ExperimentsStatisticsResponse",
                                                    ref = "#/components/examples/ExperimentsStatisticsResponse"
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
    @GetMapping(value = "/statistics")
    public ChartDto getExperimentsStatistics(
            @Parameter(description = "Experiment created date from", example = "2021-07-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateFrom,
            @Parameter(description = "Experiment created date to", example = "2021-07-10")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateTo) {
        log.info("Received request for experiment statistics calculation with creation date from [{}] to [{}]",
                createdDateFrom, createdDateTo);
        return experimentDataService.getExperimentsStatistics(createdDateFrom, createdDateTo);
    }

    /**
     * Finds experiment progress with specified id.
     *
     * @param id - experiment id
     * @return experiment progress dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Finds experiment progress with specified id",
            summary = "Finds experiment progress with specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "ExperimentProgressResponse",
                                                    ref = "#/components/examples/ExperimentProgressResponse"
                                            )
                                    },
                                    schema = @Schema(implementation = ExperimentProgressDto.class)
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
    @GetMapping(value = "/progress/{id}")
    public ExperimentProgressDto getExperimentProgress(
            @Parameter(description = "Experiment id", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        log.trace("Received request to get experiment progress for id [{}]", id);
        Experiment experiment = experimentDataService.getById(id);
        ExperimentProgressEntity experimentProgressEntity = experimentProgressService.getExperimentProgress(experiment);
        return experimentProgressMapper.map(experimentProgressEntity);
    }

    /**
     * Gets experiment results content url.
     *
     * @param id - experiment id
     * @return s3 content response dto
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets experiment results content url",
            summary = "Gets experiment results content url",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(
                                                    name = "GetExperimentResultsContentResponse",
                                                    ref = "#/components/examples/GetExperimentResultsContentResponse"
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
    @GetMapping(value = "/results-content/{id}")
    public S3ContentResponseDto getExperimentResultsContentUrl(
            @Parameter(description = "Experiment id", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        log.info("Received request to get experiment [{}] result content url", id);
        return experimentDataService.getExperimentResultsContentUrl(id);
    }

    /**
     * Gets experiment results roc curve data.
     *
     * @param experimentResultsId - experiment results id
     * @param classValueIndex     - class value index
     * @return roc curve data
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Gets experiment results roc curve data",
            summary = "Gets experiment results roc curve data",
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
    public RocCurveDataDto getRocCurveData(
            @Parameter(description = "Experiment result id", example = "1", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE)
            @RequestParam Long experimentResultsId,
            @Parameter(description = "Class value index", example = "1", required = true)
            @Min(VALUE_0) @Max(Long.MAX_VALUE)
            @RequestParam Integer classValueIndex) {
        log.info("Request to calculate roc curve data for experiment results [{}], class index [{}]",
                experimentResultsId,
                classValueIndex);
        return experimentResultsRocCurveDataProvider.getRocCurveData(experimentResultsId, classValueIndex);
    }

    /**
     * Classify instance for specified experiment results classifier model.
     *
     * @param classifyInstanceRequestDto - classify instance request dto
     * @return classify instance result
     */
    @PreAuthorize("hasAuthority('SCOPE_web')")
    @Operation(
            description = "Classify instance for specified experiment results classifier model",
            summary = "Classify instance for specified experiment results classifier model",
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
        return classifyExperimentResultsInstanceService.classifyInstance(classifyInstanceRequestDto);
    }
}
