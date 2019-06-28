package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.model.MultipartFileResource;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.ChartDataDto;
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

import javax.inject.Inject;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService        - experiment service bean
     * @param experimentRequestService - experiment request service bean
     * @param ersService               - ers service bean
     * @param experimentMapper         - experiment mapper bean
     * @param experimentRepository     - experiment repository bean
     */
    @Inject
    public ExperimentController(ExperimentService experimentService,
                                ExperimentRequestService experimentRequestService,
                                ErsService ersService, ExperimentMapper experimentMapper,
                                ExperimentRepository experimentRepository) {
        this.experimentService = experimentService;
        this.experimentRequestService = experimentRequestService;
        this.ersService = ersService;
        this.experimentMapper = experimentMapper;
        this.experimentRepository = experimentRepository;
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
    public String createRequest(
            @ApiParam(value = "Training data file", required = true) @RequestParam MultipartFile trainingData,
            @ApiParam(value = "Experiment type", required = true) @RequestParam ExperimentType experimentType,
            @ApiParam(value = "Evaluation method", required = true) @RequestParam EvaluationMethod evaluationMethod)
            throws Exception {
        log.info("Received experiment request for data '{}'", trainingData.getOriginalFilename());
        ExperimentRequest experimentRequest = new ExperimentRequest();
        experimentRequest.setFirstName("Роман");
        experimentRequest.setEmail("roman.batygin@mail.ru");
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new MultipartFileResource(trainingData));
        experimentRequest.setData(fileDataLoader.loadInstances());
        experimentRequest.setExperimentType(experimentType);
        experimentRequest.setEvaluationMethod(evaluationMethod);
        Experiment experiment = experimentRequestService.createExperimentRequest(experimentRequest);
        return experiment.getUuid();
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

}
