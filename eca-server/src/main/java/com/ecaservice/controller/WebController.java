package com.ecaservice.controller;

import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
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
    private final ExperimentMapper experimentMapper;
    private final EvaluationLogMapper evaluationLogMapper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService    - experiment service bean
     * @param evaluationLogService - evaluation log service bean
     * @param experimentMapper     - experiment mapper bean
     * @param evaluationLogMapper  - evaluation log mapper bean
     */
    @Inject
    public WebController(ExperimentService experimentService,
                         EvaluationLogService evaluationLogService,
                         ExperimentMapper experimentMapper,
                         EvaluationLogMapper evaluationLogMapper) {
        this.experimentService = experimentService;
        this.evaluationLogService = evaluationLogService;
        this.experimentMapper = experimentMapper;
        this.evaluationLogMapper = evaluationLogMapper;
    }

    /**
     * Finds experiments with specified options such as filter, sorting and paging.
     *
     * @param pageRequestDto - page request dto
     * @return experiment page dto
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
        return Utils.createRequestStatusesStatistics(experimentService.getRequestStatusesStatistics());
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
     * @return evaluation logs page
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
        return Utils.createRequestStatusesStatistics(evaluationLogService.getRequestStatusesStatistics());
    }
}
