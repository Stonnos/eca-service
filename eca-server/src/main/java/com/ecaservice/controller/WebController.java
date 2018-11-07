package com.ecaservice.controller;

import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.service.ers.ClassifierOptionsRequestService;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.ClassifierOptionsRequestDto;
import com.ecaservice.web.dto.EvaluationLogDto;
import com.ecaservice.web.dto.ExperimentDto;
import com.ecaservice.web.dto.ExperimentTypeDto;
import com.ecaservice.web.dto.PageDto;
import com.ecaservice.web.dto.PageRequestDto;
import com.ecaservice.web.dto.RequestStatusStatisticsDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService                   - experiment service bean
     * @param evaluationLogService                - evaluation log service bean
     * @param classifierOptionsRequestService     - classifier options request service bean
     * @param experimentMapper                    - experiment mapper bean
     * @param evaluationLogMapper                 - evaluation log mapper bean
     * @param classifierOptionsRequestModelMapper - classifier options request mapper bean
     */
    @Inject
    public WebController(ExperimentService experimentService,
                         EvaluationLogService evaluationLogService,
                         ClassifierOptionsRequestService classifierOptionsRequestService,
                         ExperimentMapper experimentMapper,
                         EvaluationLogMapper evaluationLogMapper,
                         ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper) {
        this.experimentService = experimentService;
        this.evaluationLogService = evaluationLogService;
        this.classifierOptionsRequestService = classifierOptionsRequestService;
        this.experimentMapper = experimentMapper;
        this.evaluationLogMapper = evaluationLogMapper;
        this.classifierOptionsRequestModelMapper = classifierOptionsRequestModelMapper;
    }

    /**
     * Finds experiments with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return experiments page dto
     */
    @ApiOperation(
            value = "Finds experiments with specified options",
            notes = "Finds experiments with specified options"
    )
    @GetMapping(value = "/experiments")
    public PageDto<ExperimentDto> getExperiments(PageRequestDto pageRequestDto) {
        log.info("Received experiments page request: {}", pageRequestDto);
        Page<Experiment> experimentPage = experimentService.getExperiments(pageRequestDto);
        List<ExperimentDto> experimentDtoList = experimentMapper.map(experimentPage.getContent());
        return PageDto.of(experimentDtoList, experimentPage.getTotalElements());
    }

    /**
     * Gets all experiments types.
     *
     * @return experiments types list
     */
    @ApiOperation(
            value = "Gets all experiments types",
            notes = "Gets all experiments types"
    )
    @GetMapping(value = "/experiment-types")
    public List<ExperimentTypeDto> getExperimentTypes() {
        return Arrays.stream(ExperimentType.values()).map(experimentType -> new ExperimentTypeDto(experimentType.name(),
                experimentType.getDescription())).collect(Collectors.toList());
    }

    /**
     * Gets experiments statistics.
     *
     * @return experiments statistics dto
     */
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
    @ApiOperation(
            value = "Downloads experiment training data by specified uuid",
            notes = "Downloads experiment training data by specified uuid"
    )
    @GetMapping(value = "/experiment-training-data/{uuid}")
    public ResponseEntity downloadTrainingData(@PathVariable String uuid) {
        File trainingDataFile = experimentService.findTrainingDataFileByUuid(uuid);
        if (trainingDataFile == null) {
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
    @ApiOperation(
            value = "Finds evaluation logs with specified options",
            notes = "Finds evaluation logs with specified options"
    )
    @GetMapping(value = "/evaluations")
    public PageDto<EvaluationLogDto> getEvaluationLogs(PageRequestDto pageRequestDto) {
        log.info("Received evaluation logs page request: {}", pageRequestDto);
        Page<EvaluationLog> evaluationLogs = evaluationLogService.getEvaluationLogs(pageRequestDto);
        List<EvaluationLogDto> evaluationLogDtoList = evaluationLogMapper.map(evaluationLogs.getContent());
        return PageDto.of(evaluationLogDtoList, evaluationLogs.getTotalElements());
    }

    /**
     * Gets experiments statistics.
     *
     * @return experiments statistics dto
     */
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
    @ApiOperation(
            value = "Finds classifiers options requests models with specified options",
            notes = "Finds classifiers options requests models with specified options"
    )
    @GetMapping(value = "/classifiers-options-requests")
    public PageDto<ClassifierOptionsRequestDto> getClassifierOptionsRequestModels(PageRequestDto pageRequestDto) {
        log.info("Received classifiers options requests models page request: {}", pageRequestDto);
        Page<ClassifierOptionsRequestModel> classifierOptionsRequestModelPage =
                classifierOptionsRequestService.getClassifierOptionsRequestModels(pageRequestDto);
        List<ClassifierOptionsRequestDto> classifierOptionsRequestDtoList =
                classifierOptionsRequestModelMapper.map(classifierOptionsRequestModelPage.getContent());
        return PageDto.of(classifierOptionsRequestDtoList, classifierOptionsRequestModelPage.getTotalElements());
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
