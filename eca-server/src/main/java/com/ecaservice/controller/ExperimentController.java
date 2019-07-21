package com.ecaservice.controller;

import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.exception.ResultsNotFoundException;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.UserService;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import com.ecaservice.web.dto.model.ErsReportDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.File;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.ecaservice.util.Utils.existsFile;
import static com.ecaservice.util.Utils.toRequestStatusesStatistics;

/**
 * Experiments API for web application.
 *
 * @author Roman Batygin
 */
@Api(tags = "Experiments API for web application")
@Slf4j
@RestController
@RequestMapping("/experiment")
public class ExperimentController {

    private final ExperimentService experimentService;
    private final ExperimentRequestService experimentRequestService;
    private final ErsService ersService;
    private final ExperimentMapper experimentMapper;
    private final UserService userService;
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;

    private final ConcurrentHashMap<String, Object> experimentMap = new ConcurrentHashMap<>();

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService                  - experiment service bean
     * @param experimentRequestService           - experiment request service bean
     * @param ersService                         - ers service bean
     * @param experimentMapper                   - experiment mapper bean
     * @param userService                        - user service bean
     * @param experimentRepository               - experiment repository bean
     * @param experimentResultsRequestRepository - experiment results request repository bean
     */
    @Inject
    public ExperimentController(ExperimentService experimentService,
                                ExperimentRequestService experimentRequestService,
                                ErsService ersService,
                                ExperimentMapper experimentMapper,
                                UserService userService,
                                ExperimentRepository experimentRepository,
                                ExperimentResultsRequestRepository experimentResultsRequestRepository) {
        this.experimentService = experimentService;
        this.experimentRequestService = experimentRequestService;
        this.ersService = ersService;
        this.experimentMapper = experimentMapper;
        this.userService = userService;
        this.experimentRepository = experimentRepository;
        this.experimentResultsRequestRepository = experimentResultsRequestRepository;
    }

    /**
     * Downloads experiment training data by specified uuid.
     *
     * @param uuid - experiment uuid
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Downloads experiment training data by specified uuid",
            notes = "Downloads experiment training data by specified uuid"
    )
    @GetMapping(value = "/training-data/{uuid}")
    public ResponseEntity downloadTrainingData(
            @ApiParam(value = "Experiment uuid", required = true) @PathVariable String uuid) {
        File trainingDataFile = experimentService.findTrainingDataFileByUuid(uuid);
        if (!existsFile(trainingDataFile)) {
            log.error("Experiment training data file for uuid = '{}' not found!", uuid);
            return ResponseEntity.badRequest().body(
                    String.format("Experiment training data file for uuid = '%s' not found!", uuid));
        }
        log.info("Download experiment training data '{}' for uuid = '{}'", trainingDataFile.getAbsolutePath(), uuid);
        return Utils.buildAttachmentResponse(trainingDataFile);
    }

    /**
     * Creates experiment request.
     *
     * @param trainingData     - training data file with format, such as csv, xls, xlsx, arff, json, docx, data, txt
     * @param experimentType   - experiment type
     * @param evaluationMethod - evaluation method
     * @return experiment uuid
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Creates experiment request with specified options",
            notes = "Creates experiment request with specified options"
    )
    @PostMapping(value = "/create")
    public CreateExperimentResultDto createRequest(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @ApiParam(value = "Experiment type", required = true) @RequestParam ExperimentType experimentType,
            @ApiParam(value = "Evaluation method", required = true) @RequestParam EvaluationMethod evaluationMethod) {
        log.info("Received experiment request for data '{}'", trainingData.getOriginalFilename());
        CreateExperimentResultDto resultDto = new CreateExperimentResultDto();
        try {
            ExperimentRequest experimentRequest =
                    createExperimentRequest(trainingData, experimentType, evaluationMethod);
            Experiment experiment = experimentRequestService.createExperimentRequest(experimentRequest);
            resultDto.setUuid(experiment.getUuid());
            resultDto.setCreated(true);
        } catch (Exception ex) {
            log.error("There was an error while experiment creation for data '{}'", trainingData.getOriginalFilename());
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
    @ApiOperation(
            value = "Finds experiments with specified options",
            notes = "Finds experiments with specified options"
    )
    @GetMapping(value = "/list")
    public PageDto<ExperimentDto> getExperiments(PageRequestDto pageRequestDto) {
        log.info("Received experiments page request: {}", pageRequestDto);
        Page<Experiment> experimentPage = experimentService.getNextPage(pageRequestDto);
        List<ExperimentDto> experimentDtoList = experimentMapper.map(experimentPage.getContent());
        return PageDto.of(experimentDtoList, pageRequestDto.getPage(), experimentPage.getTotalElements());
    }

    /**
     * Gets experiments request statuses statistics.
     *
     * @return experiments request statuses statistics dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets experiments request statuses statistics",
            notes = "Gets experiments request statuses statistics"
    )
    @GetMapping(value = "/request-statuses-statistics")
    public RequestStatusStatisticsDto getExperimentsRequestStatusesStatistics() {
        return toRequestStatusesStatistics(experimentService.getRequestStatusesStatistics());
    }

    /**
     * Gets ERS report for specified experiment.
     *
     * @param uuid - experiment uuid
     * @return ers report dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets experiment ERS report",
            notes = "Gets experiment ERS report"
    )
    @GetMapping(value = "/ers-report/{uuid}")
    public ResponseEntity<ErsReportDto> getExperimentErsReport(
            @ApiParam(value = "Experiment uuid", required = true) @PathVariable String uuid) {
        log.info("Received request for ERS report for experiment [{}]", uuid);
        Experiment experiment = experimentRepository.findByUuid(uuid);
        if (experiment == null) {
            log.error("Experiment with uuid [{}] not found", uuid);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(ersService.getErsReport(experiment));
    }

    /**
     * Sent evaluation results to ERS for experiment.
     *
     * @param uuid - experiment uuid
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Sent evaluation results to ERS for experiment",
            notes = "Sent evaluation results to ERS for experiment"
    )
    @PostMapping(value = "/sent-evaluation-results")
    public ResponseEntity sentExperimentEvaluationResults(@RequestBody String uuid) {
        log.info("Received request to send evaluation results to ERS for experiment [{}]", uuid);
        Experiment experiment = experimentRepository.findByUuid(uuid);
        if (experiment == null) {
            log.error("Experiment with uuid [{}] not found", uuid);
            return ResponseEntity.badRequest().body(String.format("Experiment with uuid [%s] not found", uuid));
        }
        if (!RequestStatus.FINISHED.equals(experiment.getExperimentStatus())) {
            log.error("Can't sent experiment [{}] results to ERS, because experiment status isn't FINISHED", uuid);
            return ResponseEntity.badRequest().body(
                    String.format("Can't sent experiment [%s] results to ERS, because experiment status isn't FINISHED",
                            uuid));
        }
        return handleExperimentResultsSending(experiment);
    }

    /**
     * Calculates experiments types counting statistics.
     *
     * @param createdDateFrom - experiment created date from
     * @param createdDateTo   - experiment created date to
     * @return chart data list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets experiment types statistics",
            notes = "Gets experiment types statistics"
    )
    @GetMapping(value = "/statistics")
    public List<ChartDataDto> getExperimentTypesStatistics(
            @ApiParam(value = "Experiment created date from") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateFrom,
            @ApiParam(value = "Experiment created date to") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdDateTo) {
        log.info("Received request for experiment types statistics calculation with creation date from [{}] to [{}]",
                createdDateFrom, createdDateTo);
        Map<ExperimentType, Long> experimentTypesMap =
                experimentService.getExperimentTypesStatistics(createdDateFrom, createdDateTo);
        return experimentTypesMap.entrySet().stream().map(
                entry -> new ChartDataDto(entry.getKey().getDescription(), entry.getValue())).collect(
                Collectors.toList());
    }

    /**
     * Handles experiment results not found exception.
     *
     * @param ex - exception
     * @return response entity
     */
    @ExceptionHandler(ResultsNotFoundException.class)
    public ResponseEntity handleResultsNotFound(ResultsNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Handles error.
     *
     * @param ex - exception
     * @return response entity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleError(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private ExperimentRequest createExperimentRequest(MultipartFile trainingData, ExperimentType experimentType,
                                                      EvaluationMethod evaluationMethod) throws Exception {
        ExperimentRequest experimentRequest = new ExperimentRequest();
        UserDetailsImpl userDetails = userService.getCurrentUser();
        experimentRequest.setFirstName(userDetails.getFirstName());
        experimentRequest.setEmail(userDetails.getEmail());
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new MultipartFileResource(trainingData));
        experimentRequest.setData(fileDataLoader.loadInstances());
        experimentRequest.setExperimentType(experimentType);
        experimentRequest.setEvaluationMethod(evaluationMethod);
        return experimentRequest;
    }

    private boolean isResultsSentToErs(Experiment experiment) {
        return experimentResultsRequestRepository.existsByExperimentAndResponseStatusIn(experiment,
                Collections.singletonList(ResponseStatus.SUCCESS));
    }

    private ResponseEntity handleExperimentResultsSending(Experiment experiment) {
        ResponseEntity responseEntity;
        experimentMap.putIfAbsent(experiment.getUuid(), new Object());
        synchronized (experimentMap.get(experiment.getUuid())) {
            if (isResultsSentToErs(experiment)) {
                responseEntity = ResponseEntity.ok(
                        String.format("Experiment [%s] results is already sent to ERS", experiment.getUuid()));
            } else if (experiment.getDeletedDate() != null) {
                log.error("Experiment [{}] results has been deleted", experiment.getUuid());
                responseEntity = ResponseEntity.badRequest().body(
                        String.format("Experiment [%s] results has been deleted", experiment.getUuid()));
            } else {
                ExperimentHistory experimentHistory = experimentService.getExperimentResults(experiment);
                ersService.sentExperimentHistory(experiment, experimentHistory,
                        ExperimentResultsRequestSource.MANUAL);
                responseEntity = ResponseEntity.ok().build();
            }
        }
        experimentMap.remove(experiment.getUuid());
        return responseEntity;
    }
}
