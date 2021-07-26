package com.ecaservice.controller.web;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.event.model.ExperimentEmailEvent;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentProgressMapper;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentProgressEntity;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.auth.UsersClient;
import com.ecaservice.service.experiment.ExperimentProgressService;
import com.ecaservice.service.experiment.ExperimentResultsService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentProgressDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.UserDto;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.config.swagger.OpenApi30Configuration.ECA_AUTHENTICATION_SECURITY_SCHEME;
import static com.ecaservice.controller.doc.ApiExamples.EXPERIMENTS_PAGE_REQUEST_JSON;
import static com.ecaservice.util.ExperimentUtils.getExperimentFile;
import static com.ecaservice.util.Utils.existsFile;
import static com.ecaservice.util.Utils.toRequestStatusesStatistics;

/**
 * Experiments API for web application.
 *
 * @author Roman Batygin
 */
@Tag(name = "Experiments API for web application")
@Slf4j
@RestController
@RequestMapping("/experiment")
@RequiredArgsConstructor
public class ExperimentController {

    private static final String EXPERIMENT_RESULTS_FILE_NOT_FOUND =
            "Experiment results file for request id = '%s' not found!";
    private static final String EXPERIMENT_TRAINING_DATA_FILE_NOT_FOUND_FORMAT =
            "Experiment training data file for request id = '%s' not found!";

    private final ExperimentService experimentService;
    private final ExperimentResultsService experimentResultsService;
    private final ExperimentMapper experimentMapper;
    private final ExperimentProgressMapper experimentProgressMapper;
    private final UsersClient usersClient;
    private final ExperimentProgressService experimentProgressService;
    private final ApplicationEventPublisher eventPublisher;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    /**
     * Downloads experiment training data by specified request id.
     *
     * @param requestId - experiment request id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Downloads experiment training data by specified request id",
            summary = "Downloads experiment training data by specified request id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/training-data/{requestId}")
    public ResponseEntity<FileSystemResource> downloadTrainingData(
            @Parameter(description = "Experiment request id", required = true)
            @PathVariable String requestId) {
        return downloadExperimentFile(requestId, Experiment::getTrainingDataAbsolutePath,
                String.format(EXPERIMENT_TRAINING_DATA_FILE_NOT_FOUND_FORMAT, requestId));
    }

    /**
     * Downloads experiment results by specified request id.
     *
     * @param requestId - experiment request id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Downloads experiment results by specified request id",
            summary = "Downloads experiment results by specified request id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/results/{requestId}")
    public ResponseEntity<FileSystemResource> downloadExperiment(
            @Parameter(description = "Experiment request id", required = true)
            @PathVariable String requestId) {
        return downloadExperimentFile(requestId, Experiment::getExperimentAbsolutePath,
                String.format(EXPERIMENT_RESULTS_FILE_NOT_FOUND, requestId));
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CreateExperimentResultDto createRequest(
            @Parameter(description = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @Parameter(description = "Experiment type", required = true) @RequestParam ExperimentType experimentType,
            @Parameter(description = "Evaluation method", required = true) @RequestParam
                    EvaluationMethod evaluationMethod) {
        log.info("Received experiment request for data '{}', experiment type {}, evaluation method {}",
                trainingData.getOriginalFilename(), experimentType, evaluationMethod);
        UserDto userDto = usersClient.getUserInfo();
        CreateExperimentResultDto resultDto = new CreateExperimentResultDto();
        try {
            ExperimentRequest experimentRequest =
                    createExperimentRequest(trainingData, userDto, experimentType, evaluationMethod);
            Experiment experiment = experimentService.createExperiment(experimentRequest);
            resultDto.setRequestId(experiment.getRequestId());
            resultDto.setCreated(true);
            eventPublisher.publishEvent(new ExperimentEmailEvent(this, experiment));
            log.info("Experiment request [{}] has been created.", experiment.getRequestId());
        } catch (Exception ex) {
            log.error("There was an error while experiment creation for data '{}': {}",
                    trainingData.getOriginalFilename(), ex.getMessage());
            resultDto.setErrorMessage(ex.getMessage());
        }
        return resultDto;
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(examples = {
                            @ExampleObject(value = EXPERIMENTS_PAGE_REQUEST_JSON)
                    })
            })
    )
    @PostMapping(value = "/list")
    public PageDto<ExperimentDto> getExperiments(@Valid @RequestBody PageRequestDto pageRequestDto) {
        log.info("Received experiments page request: {}", pageRequestDto);
        Page<Experiment> experimentPage = experimentService.getNextPage(pageRequestDto);
        List<ExperimentDto> experimentDtoList = experimentMapper.map(experimentPage.getContent());
        return PageDto.of(experimentDtoList, pageRequestDto.getPage(), experimentPage.getTotalElements());
    }

    /**
     * Finds experiment with specified request id.
     *
     * @param requestId - experiment request id
     * @return experiment dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds experiment with specified request id",
            summary = "Finds experiment with specified request id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/details/{requestId}")
    public ResponseEntity<ExperimentDto> getExperiment(
            @Parameter(description = "Experiment request id", required = true)
            @PathVariable String requestId) {
        log.info("Received request to get experiment details for request id [{}]", requestId);
        Experiment experiment = experimentService.getByRequestId(requestId);
        return ResponseEntity.ok(experimentMapper.map(experiment));
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/results/details/{id}")
    public ResponseEntity<ExperimentResultsDetailsDto> getExperimentResultsDetails(
            @Parameter(description = "Experiment results id", example = "1", required = true)
            @PathVariable Long id) {
        log.info("Received request to get experiment results details for id [{}]", id);
        ExperimentResultsEntity experimentResultsEntityOptional = experimentResultsEntityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ExperimentResultsEntity.class, id));
        return ResponseEntity.ok(experimentResultsService.getExperimentResultsDetails(experimentResultsEntityOptional));
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/request-statuses-statistics")
    public RequestStatusStatisticsDto getExperimentsRequestStatusesStatistics() {
        return toRequestStatusesStatistics(experimentService.getRequestStatusesStatistics());
    }

    /**
     * Gets ERS report for specified experiment.
     *
     * @param requestId - experiment request id
     * @return ers report dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Gets experiment ERS report",
            summary = "Gets experiment ERS report",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/ers-report/{requestId}")
    public ResponseEntity<ExperimentErsReportDto> getExperimentErsReport(
            @Parameter(description = "Experiment request id", required = true)
            @PathVariable String requestId) {
        log.info("Received request for ERS report for experiment [{}]", requestId);
        Experiment experiment = experimentService.getByRequestId(requestId);
        return ResponseEntity.ok(experimentResultsService.getErsReport(experiment));
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
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
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
     * Finds experiment progress with specified request id.
     *
     * @param requestId - experiment request id
     * @return experiment progress dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @Operation(
            description = "Finds experiment progress with specified request id",
            summary = "Finds experiment progress with specified request id",
            security = @SecurityRequirement(name = ECA_AUTHENTICATION_SECURITY_SCHEME)
    )
    @GetMapping(value = "/progress/{requestId}")
    public ResponseEntity<ExperimentProgressDto> getExperimentProgress(
            @Parameter(description = "Experiment request id", required = true)
            @PathVariable String requestId) {
        log.trace("Received request to get experiment progress for request id [{}]", requestId);
        Experiment experiment = experimentService.getByRequestId(requestId);
        ExperimentProgressEntity experimentProgressEntity = experimentProgressService.getExperimentProgress(experiment);
        return ResponseEntity.ok(experimentProgressMapper.map(experimentProgressEntity));
    }

    private ExperimentRequest createExperimentRequest(MultipartFile trainingData,
                                                      UserDto userDto,
                                                      ExperimentType experimentType,
                                                      EvaluationMethod evaluationMethod) throws Exception {
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setFirstName(userDto.getFirstName());
        experimentRequest.setEmail(userDto.getEmail());
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new MultipartFileResource(trainingData));
        experimentRequest.setData(fileDataLoader.loadInstances());
        experimentRequest.setExperimentType(experimentType);
        experimentRequest.setEvaluationMethod(evaluationMethod);
        return experimentRequest;
    }

    private ResponseEntity<FileSystemResource> downloadExperimentFile(String requestId,
                                                                      Function<Experiment, String> filePathFunction,
                                                                      String errorMessage) {
        Experiment experiment = experimentService.getByRequestId(requestId);
        File experimentFile = getExperimentFile(experiment, filePathFunction);
        if (!existsFile(experimentFile)) {
            log.error(errorMessage);
            return ResponseEntity.badRequest().build();
        }
        log.info("Downloads experiment file '{}' for request id = '{}'", filePathFunction.apply(experiment), requestId);
        return Utils.buildAttachmentResponse(experimentFile);
    }
}
