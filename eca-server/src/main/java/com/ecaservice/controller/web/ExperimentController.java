package com.ecaservice.controller.web;

import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.event.model.ExperimentResultsSendingEvent;
import com.ecaservice.exception.experiment.ExperimentException;
import com.ecaservice.exception.experiment.ResultsNotFoundException;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.UserService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentResultsLockService;
import com.ecaservice.service.experiment.ExperimentResultsService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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

import javax.validation.Valid;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ecaservice.util.ExperimentUtils.getExperimentFile;
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
@RequiredArgsConstructor
public class ExperimentController {

    private static final String EXPERIMENT_RESULTS_FILE_NOT_FOUND =
            "Experiment results file for uuid = '%s' not found!";
    private static final String EXPERIMENT_RESULTS_SENT_FORMAT = "Experiment [%s] results is already sent to ERS";
    private static final String EXPERIMENT_RESULTS_FILE_DELETED_FORMAT =
            "Experiment [%s] results file has been deleted";
    private static final String EXPERIMENT_NOT_FOUND_FORMAT = "Experiment with uuid [%s] not found";
    private static final String EXPERIMENT_NOT_FINISHED_FORMAT =
            "Can't sent experiment [%s] results to ERS, because experiment status isn't FINISHED";
    private static final String EXPERIMENT_RESULTS_NOT_FOUND = "Can't found experiment [%s] results to ERS sent";
    private static final String EXPERIMENT_TRAINING_DATA_FILE_NOT_FOUND_FORMAT =
            "Experiment training data file for uuid = '%s' not found!";

    private final ExperimentService experimentService;
    private final ExperimentRequestService experimentRequestService;
    private final ExperimentResultsService experimentResultsService;
    private final ExperimentMapper experimentMapper;
    private final UserService userService;
    private final ExperimentResultsLockService lockService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;

    private final ConcurrentHashMap<String, Object> experimentMap = new ConcurrentHashMap<>();

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
        return downloadExperimentFile(uuid, Experiment::getTrainingDataAbsolutePath,
                String.format(EXPERIMENT_TRAINING_DATA_FILE_NOT_FOUND_FORMAT, uuid));
    }

    /**
     * Downloads experiment results by specified uuid.
     *
     * @param uuid - experiment uuid
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Downloads experiment results by specified uuid",
            notes = "Downloads experiment results by specified uuid"
    )
    @GetMapping(value = "/results/{uuid}")
    public ResponseEntity downloadExperiment(
            @ApiParam(value = "Experiment uuid", required = true) @PathVariable String uuid) {
        return downloadExperimentFile(uuid, Experiment::getExperimentAbsolutePath,
                String.format(EXPERIMENT_RESULTS_FILE_NOT_FOUND, uuid));
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
    public PageDto<ExperimentDto> getExperiments(@Valid PageRequestDto pageRequestDto) {
        log.info("Received experiments page request: {}", pageRequestDto);
        Page<Experiment> experimentPage = experimentService.getNextPage(pageRequestDto);
        List<ExperimentDto> experimentDtoList = experimentMapper.map(experimentPage.getContent());
        return PageDto.of(experimentDtoList, pageRequestDto.getPage(), experimentPage.getTotalElements());
    }

    /**
     * Finds experiment with specified uuid.
     *
     * @param uuid - experiment uuid
     * @return experiment dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds experiment with specified uuid",
            notes = "Finds experiment with specified uuid"
    )
    @GetMapping(value = "/details/{uuid}")
    public ResponseEntity<ExperimentDto> getExperiment(
            @ApiParam(value = "Experiment uuid", required = true) @PathVariable String uuid) {
        Experiment experiment = experimentRepository.findByUuid(uuid);
        if (experiment == null) {
            log.error(String.format(EXPERIMENT_NOT_FOUND_FORMAT, uuid));
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(experimentMapper.map(experiment));
    }

    /**
     * Finds experiment results details with specified id.
     *
     * @param id - experiment results id
     * @return experiment results details dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds experiment results details with specified id",
            notes = "Finds experiment results details with specified id"
    )
    @GetMapping(value = "/results/details/{id}")
    public ResponseEntity<ExperimentResultsDetailsDto> getExperimentResultsDetails(
            @ApiParam(value = "Experiment results id", example = "1", required = true) @PathVariable Long id) {
        Optional<ExperimentResultsEntity> experimentResultsEntityOptional =
                experimentResultsEntityRepository.findById(id);
        if (!experimentResultsEntityOptional.isPresent()) {
            log.error("Experiment results with id [{}] not found", id);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(
                experimentResultsService.getExperimentResultsDetails(experimentResultsEntityOptional.get()));
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
    public ResponseEntity<ExperimentErsReportDto> getExperimentErsReport(
            @ApiParam(value = "Experiment uuid", required = true) @PathVariable String uuid) {
        log.info("Received request for ERS report for experiment [{}]", uuid);
        Experiment experiment = experimentRepository.findByUuid(uuid);
        if (experiment == null) {
            log.error(String.format(EXPERIMENT_NOT_FOUND_FORMAT, uuid));
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(experimentResultsService.getErsReport(experiment));
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
            String error = String.format(EXPERIMENT_NOT_FOUND_FORMAT, uuid);
            log.error(error);
            return ResponseEntity.badRequest().body(error);
        }
        if (!RequestStatus.FINISHED.equals(experiment.getExperimentStatus())) {
            log.error("Can't sent experiment [{}] results to ERS, because experiment status isn't FINISHED", uuid);
            return ResponseEntity.badRequest().body(String.format(EXPERIMENT_NOT_FINISHED_FORMAT, uuid));
        }
        long resultsCount = experimentResultsEntityRepository.countByExperiment(experiment);
        if (resultsCount == 0L) {
            log.error("Can't found experiment [{}] results to ERS sent", experiment.getUuid());
            return ResponseEntity.badRequest().body(String.format(EXPERIMENT_RESULTS_NOT_FOUND, experiment.getUuid()));
        }
        long sentResults = experimentResultsEntityRepository.getSentResultsCount(experiment);
        if (resultsCount == sentResults) {
            return ResponseEntity.ok(String.format(EXPERIMENT_RESULTS_SENT_FORMAT, experiment.getUuid()));
        } else if (experiment.getDeletedDate() != null) {
            log.error("Experiment [{}] results file has been deleted", experiment.getUuid());
            return ResponseEntity.badRequest().body(
                    String.format(EXPERIMENT_RESULTS_FILE_DELETED_FORMAT, experiment.getUuid()));
        }
        return sentExperimentResults(experiment);
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
                entry -> new ChartDataDto(entry.getKey().name(), entry.getKey().getDescription(),
                        entry.getValue())).collect(Collectors.toList());
    }

    /**
     * Handles experiment results not found exception.
     *
     * @param ex - results not found exception
     * @return response entity
     */
    @ExceptionHandler(ResultsNotFoundException.class)
    public ResponseEntity handleResultsNotFound(ResultsNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Handles experiments error.
     *
     * @param ex - experiment exception
     * @return response entity
     */
    @ExceptionHandler(ExperimentException.class)
    public ResponseEntity handleError(ExperimentException ex) {
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

    private ResponseEntity sentExperimentResults(Experiment experiment) {
        ResponseEntity responseEntity;
        experimentMap.putIfAbsent(experiment.getUuid(), new Object());
        synchronized (experimentMap.get(experiment.getUuid())) {
            if (lockService.locked(experiment.getUuid())) {
                responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else {
                applicationEventPublisher.publishEvent(
                        new ExperimentResultsSendingEvent(this, experiment, ExperimentResultsRequestSource.MANUAL));
                responseEntity = ResponseEntity.ok().build();
            }
        }
        experimentMap.remove(experiment.getUuid());
        return responseEntity;
    }

    private ResponseEntity downloadExperimentFile(String uuid, Function<Experiment, String> filePathFunction,
                                                  String errorMessage) {
        Experiment experiment = experimentRepository.findByUuid(uuid);
        if (experiment == null) {
            log.error(String.format(EXPERIMENT_NOT_FOUND_FORMAT, uuid));
            return ResponseEntity.badRequest().build();
        }
        File experimentFile = getExperimentFile(experiment, filePathFunction);
        if (!existsFile(experimentFile)) {
            log.error(errorMessage);
            return ResponseEntity.badRequest().body(errorMessage);
        }
        log.info("Downloads experiment file '{}' for uuid = '{}'", filePathFunction.apply(experiment), uuid);
        return Utils.buildAttachmentResponse(experimentFile);
    }
}
