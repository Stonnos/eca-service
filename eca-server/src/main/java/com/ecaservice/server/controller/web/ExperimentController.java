package com.ecaservice.server.controller.web;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.event.model.ExperimentEmailEvent;
import com.ecaservice.server.mapping.ExperimentMapper;
import com.ecaservice.server.mapping.ExperimentProgressMapper;
import com.ecaservice.server.model.MsgProperties;
import com.ecaservice.server.model.MultipartFileResource;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.ExperimentProgressEntity;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.repository.ExperimentResultsEntityRepository;
import com.ecaservice.server.service.auth.UsersClient;
import com.ecaservice.server.service.experiment.DataService;
import com.ecaservice.server.service.experiment.ExperimentProgressService;
import com.ecaservice.server.service.experiment.ExperimentResultsService;
import com.ecaservice.server.service.experiment.ExperimentService;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentProgressDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.ExperimentsPageDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.UserDto;
import eca.core.evaluation.EvaluationMethod;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import weka.core.Instances;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.config.swagger.OpenApi30Configuration.SCOPE_WEB;
import static com.ecaservice.server.controller.doc.ApiExamples.CREATE_EXPERIMENT_RESULT_RESPONSE_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.EXPERIMENTS_PAGE_REQUEST_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.EXPERIMENTS_PAGE_RESPONSE_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.EXPERIMENT_DETAILS_RESPONSE_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.EXPERIMENT_ERS_REPORT_RESPONSE_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.EXPERIMENT_PROGRESS_RESPONSE_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.EXPERIMENT_RESULTS_DETAILS_RESPONSE_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.EXPERIMENT_TYPES_STATISTICS_RESPONSE_JSON;
import static com.ecaservice.server.controller.doc.ApiExamples.REQUESTS_STATUSES_STATISTICS_RESPONSE_JSON;
import static com.ecaservice.server.util.ExperimentUtils.getExperimentFile;
import static com.ecaservice.server.util.Utils.buildAttachmentResponse;
import static com.ecaservice.server.util.Utils.existsFile;
import static com.ecaservice.server.util.Utils.toRequestStatusesStatistics;
import static com.ecaservice.web.dto.doc.CommonApiExamples.DATA_NOT_FOUND_RESPONSE_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.INVALID_PAGE_REQUEST_RESPONSE_JSON;
import static com.ecaservice.web.dto.doc.CommonApiExamples.UNAUTHORIZED_RESPONSE_JSON;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
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

    private static final String EXPERIMENT_RESULTS_FILE_NOT_FOUND =
            "Experiment results file for id = '%d' not found!";
    private static final String EXPERIMENT_TRAINING_DATA_FILE_NOT_FOUND_FORMAT =
            "Experiment training data file for id = '%d' not found!";

    private final ExperimentService experimentService;
    private final ExperimentResultsService experimentResultsService;
    private final ExperimentMapper experimentMapper;
    private final ExperimentProgressMapper experimentProgressMapper;
    private final UsersClient usersClient;
    private final ExperimentProgressService experimentProgressService;
    private final ApplicationEventPublisher eventPublisher;
    private final DataService dataService;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    /**
     * Downloads experiment training data by specified id.
     *
     * @param id - experiment id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Downloads experiment training data by specified id",
            summary = "Downloads experiment training data by specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/training-data/{id}")
    public ResponseEntity<FileSystemResource> downloadTrainingData(
            @Parameter(description = "Experiment id", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        return downloadExperimentFile(id, Experiment::getTrainingDataAbsolutePath,
                String.format(EXPERIMENT_TRAINING_DATA_FILE_NOT_FOUND_FORMAT, id));
    }

    /**
     * Downloads experiment results by specified id.
     *
     * @param id - experiment id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Downloads experiment results by specified id",
            summary = "Downloads experiment results by specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @GetMapping(value = "/results/{id}")
    public ResponseEntity<FileSystemResource> downloadExperiment(
            @Parameter(description = "Experiment id", required = true)
            @Min(VALUE_1) @Max(Long.MAX_VALUE) @PathVariable Long id) {
        return downloadExperimentFile(id, Experiment::getExperimentAbsolutePath,
                String.format(EXPERIMENT_RESULTS_FILE_NOT_FOUND, id));
    }

    /**
     * Creates experiment request.
     *
     * @param trainingData     - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param experimentType   - experiment type
     * @param evaluationMethod - evaluation method
     * @return create experiment results dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Creates experiment request with specified options",
            summary = "Creates experiment request with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = CREATE_EXPERIMENT_RESULT_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = CreateExperimentResultDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    )
            }
    )
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateExperimentResultDto createRequest(
            @Parameter(description = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @Parameter(description = "Experiment type", required = true,
                    schema = @Schema(maxLength = MAX_LENGTH_255))
            @RequestParam ExperimentType experimentType,
            @Parameter(description = "Evaluation method", required = true,
                    schema = @Schema(maxLength = MAX_LENGTH_255))
            @RequestParam EvaluationMethod evaluationMethod) {
        log.info("Received experiment request for data '{}', experiment type {}, evaluation method {}",
                trainingData.getOriginalFilename(), experimentType, evaluationMethod);
        UserDto userDto = usersClient.getUserInfo();
        ExperimentRequest experimentRequest =
                createExperimentRequest(trainingData, userDto, experimentType, evaluationMethod);
        MsgProperties msgProperties = MsgProperties.builder()
                .channel(Channel.WEB)
                .build();
        Experiment experiment = experimentService.createExperiment(experimentRequest, msgProperties);
        eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
        log.info("Experiment request [{}] has been created.", experiment.getRequestId());
        return CreateExperimentResultDto.builder()
                .id(experiment.getId())
                .requestId(experiment.getRequestId())
                .build();
    }

    /**
     * Finds experiments with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return experiments page dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds experiments with specified options",
            summary = "Finds experiments with specified options",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = EXPERIMENTS_PAGE_REQUEST_JSON)
                    })
            }),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = EXPERIMENTS_PAGE_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = ExperimentsPageDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = INVALID_PAGE_REQUEST_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ValidationErrorDto.class))
                            )
                    )
            }
    )
    @PostMapping(value = "/list")
    public PageDto<ExperimentDto> getExperiments(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received experiments page request: {}", pageRequestDto);
        Page<Experiment> experimentPage = experimentService.getNextPage(pageRequestDto);
        List<ExperimentDto> experimentDtoList = experimentMapper.map(experimentPage.getContent());
        return PageDto.of(experimentDtoList, pageRequestDto.getPage(), experimentPage.getTotalElements());
    }

    /**
     * Finds experiment with specified id.
     *
     * @param id - experiment id
     * @return experiment dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds experiment with specified id",
            summary = "Finds experiment with specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = EXPERIMENT_DETAILS_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = ExperimentDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
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
        Experiment experiment = experimentService.getById(id);
        return experimentMapper.map(experiment);
    }

    /**
     * Finds experiment results details with specified id.
     *
     * @param id - experiment results id
     * @return experiment results details dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds experiment results details with specified id",
            summary = "Finds experiment results details with specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = EXPERIMENT_RESULTS_DETAILS_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = ExperimentResultsDetailsDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
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
        ExperimentResultsEntity experimentResultsEntityOptional = experimentResultsEntityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentResultsEntity.class, id));
        return experimentResultsService.getExperimentResultsDetails(experimentResultsEntityOptional);
    }

    /**
     * Gets experiments request statuses statistics.
     *
     * @return experiments request statuses statistics dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets experiments request statuses statistics",
            summary = "Gets experiments request statuses statistics",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = REQUESTS_STATUSES_STATISTICS_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = RequestStatusStatisticsDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/request-statuses-statistics")
    public RequestStatusStatisticsDto getExperimentsRequestStatusesStatistics() {
        log.info("Request get experiments statuses statistics");
        var requestStatusStatisticsDto = toRequestStatusesStatistics(experimentService.getRequestStatusesStatistics());
        log.info("Experiments statuses statistics: {}", requestStatusStatisticsDto);
        return requestStatusStatisticsDto;
    }

    /**
     * Gets ERS report for specified experiment.
     *
     * @param id - experiment id
     * @return ers report dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets experiment ERS report",
            summary = "Gets experiment ERS report",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = EXPERIMENT_ERS_REPORT_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = ExperimentErsReportDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
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
        Experiment experiment = experimentService.getById(id);
        return experimentResultsService.getErsReport(experiment);
    }

    /**
     * Calculates experiments types counting statistics.
     *
     * @param createdDateFrom - experiment created date from
     * @param createdDateTo   - experiment created date to
     * @return chart data list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets experiment types statistics",
            summary = "Gets experiment types statistics",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = EXPERIMENT_TYPES_STATISTICS_RESPONSE_JSON),
                                    },
                                    array = @ArraySchema(schema = @Schema(implementation = ChartDataDto.class))
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    )
            }
    )
    @GetMapping(value = "/statistics")
    public List<ChartDataDto> getExperimentTypesStatistics(
            @Parameter(description = "Experiment created date from", example = "2021-07-01")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateFrom,
            @Parameter(description = "Experiment created date to", example = "2021-07-10")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateTo) {
        log.info("Received request for experiment types statistics calculation with creation date from [{}] to [{}]",
                createdDateFrom, createdDateTo);
        Map<ExperimentType, Long> experimentTypesMap =
                experimentService.getExperimentTypesStatistics(createdDateFrom, createdDateTo);
        return experimentTypesMap.entrySet()
                .stream()
                .map(entry -> new ChartDataDto(entry.getKey().name(), entry.getKey().getDescription(),
                        entry.getValue())).collect(Collectors.toList());
    }

    /**
     * Finds experiment progress with specified id.
     *
     * @param id - experiment id
     * @return experiment progress dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds experiment progress with specified id",
            summary = "Finds experiment progress with specified id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME, scopes = SCOPE_WEB),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = EXPERIMENT_PROGRESS_RESPONSE_JSON),
                                    },
                                    schema = @Schema(implementation = ExperimentProgressDto.class)
                            )
                    ),
                    @ApiResponse(description = "Not authorized", responseCode = "401",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = UNAUTHORIZED_RESPONSE_JSON),
                                    }
                            )
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "400",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = {
                                            @ExampleObject(value = DATA_NOT_FOUND_RESPONSE_JSON),
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
        Experiment experiment = experimentService.getById(id);
        ExperimentProgressEntity experimentProgressEntity = experimentProgressService.getExperimentProgress(experiment);
        return experimentProgressMapper.map(experimentProgressEntity);
    }

    private ExperimentRequest createExperimentRequest(MultipartFile trainingData,
                                                      UserDto userDto,
                                                      ExperimentType experimentType,
                                                      EvaluationMethod evaluationMethod) {
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setFirstName(userDto.getFirstName());
        experimentRequest.setEmail(userDto.getEmail());
        Instances data = dataService.load(new MultipartFileResource(trainingData));
        experimentRequest.setData(data);
        experimentRequest.setExperimentType(experimentType);
        experimentRequest.setEvaluationMethod(evaluationMethod);
        return experimentRequest;
    }

    private ResponseEntity<FileSystemResource> downloadExperimentFile(Long id,
                                                                      Function<Experiment, String> filePathFunction,
                                                                      String errorMessage) {
        Experiment experiment = experimentService.getById(id);
        File experimentFile = getExperimentFile(experiment, filePathFunction);
        if (!existsFile(experimentFile)) {
            log.error(errorMessage);
            return ResponseEntity.badRequest().build();
        }
        log.info("Downloads experiment file '{}' for id = '{}'", filePathFunction.apply(experiment), id);
        return buildAttachmentResponse(experimentFile);
    }
}
