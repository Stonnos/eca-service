package com.ecaservice.controller;

import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.mapping.ErsResponseStatusMapper;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.ErsReportDto;
import com.ecaservice.web.dto.model.ErsResponseStatus;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.UserDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.util.Utils.existsFile;

/**
 * Rest controller providing operations for web application.
 */
@Api(tags = "Operations for web application")
@Slf4j
@RestController
public class WebController {

    private final ExperimentService experimentService;
    private final EvaluationLogService evaluationLogService;
    private final ClassifierOptionsRequestService classifierOptionsRequestService;
    private final ExperimentMapper experimentMapper;
    private final EvaluationLogMapper evaluationLogMapper;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ErsResponseStatusMapper ersResponseStatusMapper;
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService                   - experiment service bean
     * @param evaluationLogService                - evaluation log service bean
     * @param classifierOptionsRequestService     - classifier options request service bean
     * @param experimentMapper                    - experiment mapper bean
     * @param evaluationLogMapper                 - evaluation log mapper bean
     * @param classifierOptionsRequestModelMapper - classifier options request mapper bean
     * @param ersResponseStatusMapper             - ers response status mapper bean
     * @param experimentRepository                - experiment repository bean
     * @param experimentResultsRequestRepository  - experiment results requests repository bean
     */
    @Inject
    public WebController(ExperimentService experimentService,
                         EvaluationLogService evaluationLogService,
                         ClassifierOptionsRequestService classifierOptionsRequestService,
                         ExperimentMapper experimentMapper,
                         EvaluationLogMapper evaluationLogMapper,
                         ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper,
                         ErsResponseStatusMapper ersResponseStatusMapper,
                         ExperimentRepository experimentRepository,
                         ExperimentResultsRequestRepository experimentResultsRequestRepository) {
        this.experimentService = experimentService;
        this.evaluationLogService = evaluationLogService;
        this.classifierOptionsRequestService = classifierOptionsRequestService;
        this.experimentMapper = experimentMapper;
        this.evaluationLogMapper = evaluationLogMapper;
        this.classifierOptionsRequestModelMapper = classifierOptionsRequestModelMapper;
        this.ersResponseStatusMapper = ersResponseStatusMapper;
        this.experimentRepository = experimentRepository;
        this.experimentResultsRequestRepository = experimentResultsRequestRepository;
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
     * Gets all experiments types.
     *
     * @return experiments types list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets all experiments types",
            notes = "Gets all experiments types"
    )
    @GetMapping(value = "/experiment-types")
    public List<EnumDto> getExperimentTypes() {
        return Arrays.stream(ExperimentType.values()).map(experimentType -> new EnumDto(experimentType.name(),
                experimentType.getDescription())).collect(Collectors.toList());
    }

    /**
     * Gets all ERS responses types
     *
     * @return ERS responses list
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets all ERS responses types",
            notes = "Gets all ERS responses types"
    )
    @GetMapping(value = "/ers-responses-types")
    public List<EnumDto> getErsResponsesTypes() {
        return Arrays.stream(ResponseStatus.values()).map(responseStatus -> {
            ErsResponseStatus ersResponseStatus = ersResponseStatusMapper.map(responseStatus);
            return new EnumDto(ersResponseStatus.name(), ersResponseStatus.getDescription());
        }).collect(Collectors.toList());
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
    public ResponseEntity downloadTrainingData(@PathVariable String uuid) {
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
    public ResponseEntity<ErsReportDto> getExperimentErsReport(@PathVariable String uuid) {
        log.info("Received request for ERS report for experiment [{}]", uuid);
        Experiment experiment = experimentRepository.findByUuid(uuid);
        if (experiment == null) {
            log.error("Experiment with uuid [{}] not found", uuid);
            return ResponseEntity.badRequest().build();
        }
        List<ExperimentResultsRequest> experimentResultsRequests =
                experimentResultsRequestRepository.findAllByExperiment(experiment);
        ErsReportDto ersReportDto = new ErsReportDto();
        ersReportDto.setExperimentUuid(experiment.getUuid());
        if (!CollectionUtils.isEmpty(experimentResultsRequests)) {
            ersReportDto.setRequestsCount(experimentResultsRequests.size());
            ersReportDto.setSuccessfullySavedClassifiers(experimentResultsRequests.stream().filter(
                    experimentResultsRequest -> ResponseStatus.SUCCESS.equals(
                            experimentResultsRequest.getResponseStatus())).count());
            ersReportDto.setFailedRequestsCount(experimentResultsRequests.stream().filter(
                    experimentResultsRequest -> !ResponseStatus.SUCCESS.equals(
                            experimentResultsRequest.getResponseStatus())).count());
        }
        return ResponseEntity.ok(ersReportDto);
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
    public ResponseEntity sentExperimentEvaluationResults(@RequestParam String uuid) {
        log.info("Received request to send evaluation results to ERS for experiment [{}]", uuid);
        return ResponseEntity.ok().build();
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
