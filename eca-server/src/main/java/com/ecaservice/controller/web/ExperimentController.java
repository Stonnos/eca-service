package com.ecaservice.controller.web;

import com.ecaservice.base.model.ExperimentRequest;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentProgressMapper;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentProgressEntity;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.repository.ExperimentProgressRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.UserService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentResultsService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.user.model.UserDetailsImpl;
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
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.FileDataLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
            "Experiment results file for request id = '%s' not found!";
    private static final String EXPERIMENT_NOT_FOUND_FORMAT = "Experiment with request id [{}] not found";
    private static final String EXPERIMENT_TRAINING_DATA_FILE_NOT_FOUND_FORMAT =
            "Experiment training data file for request id = '%s' not found!";

    private final ExperimentService experimentService;
    private final ExperimentRequestService experimentRequestService;
    private final ExperimentResultsService experimentResultsService;
    private final ExperimentMapper experimentMapper;
    private final ExperimentProgressMapper experimentProgressMapper;
    private final UserService userService;
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsEntityRepository experimentResultsEntityRepository;
    private final ExperimentProgressRepository experimentProgressRepository;

    /**
     * Downloads experiment training data by specified request id.
     *
     * @param requestId - experiment request id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Downloads experiment training data by specified request id",
            notes = "Downloads experiment training data by specified request id"
    )
    @GetMapping(value = "/training-data/{requestId}")
    public ResponseEntity<FileSystemResource> downloadTrainingData(
            @ApiParam(value = "Experiment request id", required = true) @PathVariable String requestId) {
        return downloadExperimentFile(requestId, Experiment::getTrainingDataAbsolutePath,
                String.format(EXPERIMENT_TRAINING_DATA_FILE_NOT_FOUND_FORMAT, requestId));
    }

    /**
     * Downloads experiment results by specified request id.
     *
     * @param requestId - experiment request id
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Downloads experiment results by specified request id",
            notes = "Downloads experiment results by specified request id"
    )
    @GetMapping(value = "/results/{requestId}")
    public ResponseEntity<FileSystemResource> downloadExperiment(
            @ApiParam(value = "Experiment request id", required = true) @PathVariable String requestId) {
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
    @ApiOperation(
            value = "Creates experiment request with specified options",
            notes = "Creates experiment request with specified options"
    )
    @PostMapping(value = "/create")
    public CreateExperimentResultDto createRequest(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @ApiParam(value = "Experiment type", required = true) @RequestParam ExperimentType experimentType,
            @ApiParam(value = "Evaluation method", required = true) @RequestParam EvaluationMethod evaluationMethod) {
        log.info("Received experiment request for data '{}', experiment type {}, evaluation method {}",
                trainingData.getOriginalFilename(), experimentType, evaluationMethod);
        CreateExperimentResultDto resultDto = new CreateExperimentResultDto();
        try {
            ExperimentRequest experimentRequest =
                    createExperimentRequest(trainingData, experimentType, evaluationMethod);
            Experiment experiment = experimentRequestService.createExperimentRequest(experimentRequest);
            resultDto.setRequestId(experiment.getRequestId());
            resultDto.setCreated(true);
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
     * Finds experiment with specified request id.
     *
     * @param requestId - experiment request id
     * @return experiment dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds experiment with specified request id",
            notes = "Finds experiment with specified request id"
    )
    @GetMapping(value = "/details/{requestId}")
    public ResponseEntity<ExperimentDto> getExperiment(
            @ApiParam(value = "Experiment request id", required = true) @PathVariable String requestId) {
        log.info("Received request to get experiment details for request id [{}]", requestId);
        Experiment experiment = experimentRepository.findByRequestId(requestId);
        if (experiment == null) {
            log.error(EXPERIMENT_NOT_FOUND_FORMAT, requestId);
            return ResponseEntity.notFound().build();
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
        log.info("Received request to get experiment results details for id [{}]", id);
        Optional<ExperimentResultsEntity> experimentResultsEntityOptional =
                experimentResultsEntityRepository.findById(id);
        if (!experimentResultsEntityOptional.isPresent()) {
            log.error("Experiment results with id [{}] not found", id);
            return ResponseEntity.notFound().build();
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
     * @param requestId - experiment request id
     * @return ers report dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets experiment ERS report",
            notes = "Gets experiment ERS report"
    )
    @GetMapping(value = "/ers-report/{requestId}")
    public ResponseEntity<ExperimentErsReportDto> getExperimentErsReport(
            @ApiParam(value = "Experiment request id", required = true) @PathVariable String requestId) {
        log.info("Received request for ERS report for experiment [{}]", requestId);
        Experiment experiment = experimentRepository.findByRequestId(requestId);
        if (experiment == null) {
            log.error(EXPERIMENT_NOT_FOUND_FORMAT, requestId);
            return ResponseEntity.notFound().build();
        }
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
     * Finds experiment progress with specified request id.
     *
     * @param requestId - experiment request id
     * @return experiment progress dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds experiment progress with specified request id",
            notes = "Finds experiment progress with specified request id"
    )
    @GetMapping(value = "/progress/{requestId}")
    public ResponseEntity<ExperimentProgressDto> getExperimentProgress(
            @ApiParam(value = "Experiment request id", required = true) @PathVariable String requestId) {
        log.trace("Received request to get experiment progress for request id [{}]", requestId);
        Experiment experiment = experimentRepository.findByRequestId(requestId);
        if (experiment == null) {
            log.error(EXPERIMENT_NOT_FOUND_FORMAT, requestId);
            return ResponseEntity.notFound().build();
        }
        ExperimentProgressEntity experimentProgressEntity = experimentProgressRepository.findByExperiment(experiment);
        if (experimentProgressEntity == null) {
            log.error("Can't find experiment progress for request id [{}]", experiment.getRequestId());
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(experimentProgressMapper.map(experimentProgressEntity));
    }

    private ExperimentRequest createExperimentRequest(MultipartFile trainingData, ExperimentType experimentType,
                                                      EvaluationMethod evaluationMethod) throws Exception {
        ExperimentRequest experimentRequest = new ExperimentRequest();
        //FIXME call getUserInfo method
       // UserDetailsImpl userDetails = userService.getCurrentUser();
       // experimentRequest.setFirstName(userDetails.getFirstName());
      //  experimentRequest.setEmail(userDetails.getEmail());
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
        Experiment experiment = experimentRepository.findByRequestId(requestId);
        if (experiment == null) {
            log.error(EXPERIMENT_NOT_FOUND_FORMAT, requestId);
            return ResponseEntity.notFound().build();
        }
        File experimentFile = getExperimentFile(experiment, filePathFunction);
        if (!existsFile(experimentFile)) {
            log.error(errorMessage);
            return ResponseEntity.notFound().build();
        }
        log.info("Downloads experiment file '{}' for request id = '{}'", filePathFunction.apply(experiment), requestId);
        return Utils.buildAttachmentResponse(experimentFile);
    }
}
