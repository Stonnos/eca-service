package com.ecaservice.controller;

import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.ErsReportDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.UserDto;
import eca.converters.model.ExperimentHistory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.util.Utils.existsFile;

/**
 * Rest controller providing operations for web application.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for web application")
@Slf4j
@RestController
public class WebController {

    private final ExperimentService experimentService;
    private final EvaluationLogService evaluationLogService;
    private final ClassifierOptionsRequestService classifierOptionsRequestService;
    private final ErsService ersService;
    private final ExperimentMapper experimentMapper;
    private final EvaluationLogMapper evaluationLogMapper;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService                   - experiment service bean
     * @param evaluationLogService                - evaluation log service bean
     * @param classifierOptionsRequestService     - classifier options request service bean
     * @param ersService                          - ers service bean
     * @param experimentMapper                    - experiment mapper bean
     * @param evaluationLogMapper                 - evaluation log mapper bean
     * @param classifierOptionsRequestModelMapper - classifier options request mapper bean
     * @param experimentRepository                - experiment repository bean
     */
    @Inject
    public WebController(ExperimentService experimentService,
                         EvaluationLogService evaluationLogService,
                         ClassifierOptionsRequestService classifierOptionsRequestService,
                         ErsService ersService, ExperimentMapper experimentMapper,
                         EvaluationLogMapper evaluationLogMapper,
                         ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper,
                         ExperimentRepository experimentRepository) {
        this.experimentService = experimentService;
        this.evaluationLogService = evaluationLogService;
        this.classifierOptionsRequestService = classifierOptionsRequestService;
        this.ersService = ersService;
        this.experimentMapper = experimentMapper;
        this.evaluationLogMapper = evaluationLogMapper;
        this.classifierOptionsRequestModelMapper = classifierOptionsRequestModelMapper;
        this.experimentRepository = experimentRepository;
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
    @GetMapping(value = "/experiments")
    public PageDto<ExperimentDto> getExperiments(PageRequestDto pageRequestDto) {
        log.info("Received experiments page request: {}", pageRequestDto);
        Page<Experiment> experimentPage = experimentService.getNextPage(pageRequestDto);
        List<ExperimentDto> experimentDtoList = experimentMapper.map(experimentPage.getContent());
        return PageDto.of(experimentDtoList, pageRequestDto.getPage(), experimentPage.getTotalElements());
    }

    /**
     * Gets experiments statistics.
     *
     * @return experiments statistics dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets experiments statistics",
            notes = "Gets experiments statistics"
    )
    @GetMapping(value = "/experiment/request-statuses-statistics")
    public RequestStatusStatisticsDto getExperimentsRequestStatusesStatistics() {
        return createRequestStatusesStatistics(experimentService.getRequestStatusesStatistics());
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
    @GetMapping(value = "/experiment-training-data/{uuid}")
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
     * Finds evaluation logs with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return evaluations logs page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds evaluation logs with specified options",
            notes = "Finds evaluation logs with specified options"
    )
    @GetMapping(value = "/evaluations")
    public PageDto<EvaluationLogDto> getEvaluationLogs(PageRequestDto pageRequestDto) {
        log.info("Received evaluation logs page request: {}", pageRequestDto);
        Page<EvaluationLog> evaluationLogs = evaluationLogService.getNextPage(pageRequestDto);
        List<EvaluationLogDto> evaluationLogDtoList = evaluationLogMapper.map(evaluationLogs.getContent());
        return PageDto.of(evaluationLogDtoList, pageRequestDto.getPage(), evaluationLogs.getTotalElements());
    }

    /**
     * Gets experiments statistics.
     *
     * @return experiments statistics dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets experiments statistics",
            notes = "Gets experiments statistics"
    )
    @GetMapping(value = "/evaluation/request-statuses-statistics")
    public RequestStatusStatisticsDto getEvaluationRequestStatusesStatistics() {
        return createRequestStatusesStatistics(evaluationLogService.getRequestStatusesStatistics());
    }

    /**
     * Finds classifiers options requests models with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return classifiers options requests models page
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Finds classifiers options requests models with specified options",
            notes = "Finds classifiers options requests models with specified options"
    )
    @GetMapping(value = "/classifiers-options-requests")
    public PageDto<ClassifierOptionsRequestDto> getClassifierOptionsRequestModels(PageRequestDto pageRequestDto) {
        log.info("Received classifiers options requests models page request: {}", pageRequestDto);
        Page<ClassifierOptionsRequestModel> classifierOptionsRequestModelPage =
                classifierOptionsRequestService.getNextPage(pageRequestDto);
        List<ClassifierOptionsRequestDto> classifierOptionsRequestDtoList =
                classifierOptionsRequestModelMapper.map(classifierOptionsRequestModelPage.getContent());
        return PageDto.of(classifierOptionsRequestDtoList, pageRequestDto.getPage(),
                classifierOptionsRequestModelPage.getTotalElements());
    }

    /**
     * Gets current user.
     *
     * @return current user
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets current user info",
            notes = "Gets current user info"
    )
    @GetMapping(value = "/current-user")
    public UserDto getCurrentUser() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new UserDto(principal);
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
    @GetMapping(value = "/experiment-ers-report/{uuid}")
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
    @PostMapping(value = "/sent-experiment-evaluation-results")
    public ResponseEntity sentExperimentEvaluationResults(@RequestBody String uuid) {
        log.info("Received request to send evaluation results to ERS for experiment [{}]", uuid);
        Experiment experiment = experimentRepository.findByUuid(uuid);
        if (experiment == null) {
            log.error("Experiment with uuid [{}] not found", uuid);
            return ResponseEntity.badRequest().build();
        }
        try {
            ExperimentHistory experimentHistory = experimentService.getExperimentResults(uuid);
            ersService.sentExperimentHistory(experiment, experimentHistory, ExperimentResultsRequestSource.MANUAL);
        } catch (Exception ex) {
            log.error("There was an error while sending experiment history [{}]: {}", uuid, ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
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
    @GetMapping(value = "/experiment/statistics")
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

    private RequestStatusStatisticsDto createRequestStatusesStatistics(Map<RequestStatus, Long> statusStatisticsMap) {
        RequestStatusStatisticsDto requestStatusStatisticsDto = new RequestStatusStatisticsDto();
        requestStatusStatisticsDto.setNewRequestsCount(statusStatisticsMap.get(RequestStatus.NEW));
        requestStatusStatisticsDto.setFinishedRequestsCount(statusStatisticsMap.get(RequestStatus.FINISHED));
        requestStatusStatisticsDto.setTimeoutRequestsCount(statusStatisticsMap.get(RequestStatus.TIMEOUT));
        requestStatusStatisticsDto.setErrorRequestsCount(statusStatisticsMap.get(RequestStatus.ERROR));
        requestStatusStatisticsDto.setTotalCount(
                statusStatisticsMap.values().stream().mapToLong(Long::longValue).sum());
        return requestStatusStatisticsDto;
    }
}
