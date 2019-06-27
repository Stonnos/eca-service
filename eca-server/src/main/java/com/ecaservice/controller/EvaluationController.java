package com.ecaservice.controller;

import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.async.AsyncTaskService;
import com.ecaservice.service.ers.ErsRequestService;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.util.Utils.buildErrorResponse;
import static com.ecaservice.util.Utils.toRequestStatusesStatistics;

/**
 * Implements the main rest controller for processing input requests.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for individual and ensemble classifiers learning")
@Slf4j
@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    private final EvaluationRequestService evaluationRequestService;
    private final ErsRequestService ersRequestService;
    private final EvaluationOptimizerService evaluationOptimizerService;
    private final EvaluationLogService evaluationLogService;
    private final ErsService ersService;
    private final EvaluationLogMapper evaluationLogMapper;
    private final AsyncTaskService asyncTaskService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Constructor with dependency spring injection.
     *
     * @param evaluationRequestService   - evaluation request service bean
     * @param ersRequestService          - ers request service bean
     * @param evaluationOptimizerService - evaluation optimizer service bean
     * @param evaluationLogService       - evaluation log service bean
     * @param ersService                 - ers service bean
     * @param evaluationLogMapper        - evaluation log mapper bean
     * @param asyncTaskService           - async task service bean
     * @param evaluationLogRepository    - evaluation log repository bean
     */
    @Inject
    public EvaluationController(EvaluationRequestService evaluationRequestService,
                                ErsRequestService ersRequestService,
                                EvaluationOptimizerService evaluationOptimizerService,
                                EvaluationLogService evaluationLogService,
                                ErsService ersService, EvaluationLogMapper evaluationLogMapper,
                                AsyncTaskService asyncTaskService,
                                EvaluationLogRepository evaluationLogRepository) {
        this.evaluationRequestService = evaluationRequestService;
        this.ersRequestService = ersRequestService;
        this.evaluationOptimizerService = evaluationOptimizerService;
        this.evaluationLogService = evaluationLogService;
        this.ersService = ersService;
        this.evaluationLogMapper = evaluationLogMapper;
        this.asyncTaskService = asyncTaskService;
        this.evaluationLogRepository = evaluationLogRepository;
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
    @GetMapping(value = "/list")
    public PageDto<EvaluationLogDto> getEvaluationLogs(PageRequestDto pageRequestDto) {
        log.info("Received evaluation logs page request: {}", pageRequestDto);
        Page<EvaluationLog> evaluationLogs = evaluationLogService.getNextPage(pageRequestDto);
        List<EvaluationLogDto> evaluationLogDtoList = evaluationLogMapper.map(evaluationLogs.getContent());
        return PageDto.of(evaluationLogDtoList, pageRequestDto.getPage(), evaluationLogs.getTotalElements());
    }

    /**
     * Gets evaluation log details.
     *
     * @param requestId - evaluation log request id
     * @return evaluation log details report
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets evaluation log details",
            notes = "Gets evaluation log details"
    )
    @GetMapping(value = "/details/{requestId}")
    public ResponseEntity<EvaluationLogDetailsDto> getEvaluationLogDetails(
            @ApiParam(value = "Evaluation log request id", required = true) @PathVariable String requestId) {
        log.info("Received request for evaluation log details for request id [{}]", requestId);
        EvaluationLog evaluationLog = evaluationLogRepository.findByRequestId(requestId);
        if (evaluationLog == null) {
            log.error("Evaluation log with request id [{}] not found", requestId);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(ersService.getEvaluationLogDetails(evaluationLog));
    }

    /**
     * Gets evaluations request statuses statistics.
     *
     * @return evaluations request statuses statistics dto
     */
    @PreAuthorize("#oauth2.hasScope('web')")
    @ApiOperation(
            value = "Gets evaluations request statuses statistics",
            notes = "Gets evaluations request statuses statistics"
    )
    @GetMapping(value = "/request-statuses-statistics")
    public RequestStatusStatisticsDto getEvaluationRequestStatusesStatistics() {
        return toRequestStatusesStatistics(evaluationLogService.getRequestStatusesStatistics());
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param evaluationRequest - evaluation request dto
     * @return response entity
     */
    @ApiOperation(
            value = "Evaluates classifier using specified evaluation method",
            notes = "Evaluates classifier using specified evaluation method"
    )
    @PostMapping(value = "/execute")
    public ResponseEntity<EvaluationResponse> execute(@RequestBody EvaluationRequest evaluationRequest) {
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(evaluationRequest);
        log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                evaluationResponse.getStatus());
        sendEvaluationResultsToErs(evaluationResponse);
        return ResponseEntity.ok(evaluationResponse);
    }

    /**
     * Evaluates classifier using optimal options.
     *
     * @param instancesRequest - instances request
     * @return response entity
     */
    @ApiOperation(
            value = "Evaluates classifier using optimal options",
            notes = "Evaluates classifier using optimal options"
    )
    @PostMapping(value = "/optimize")
    public ResponseEntity<EvaluationResponse> optimize(@RequestBody InstancesRequest instancesRequest) {
        try {
            EvaluationResponse evaluationResponse =
                    evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
            log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                    evaluationResponse.getStatus());
            sendEvaluationResultsToErs(evaluationResponse);
            return ResponseEntity.ok(evaluationResponse);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
        }
    }

    private void sendEvaluationResultsToErs(EvaluationResponse evaluationResponse) {
        asyncTaskService.perform(() -> {
            EvaluationLog evaluationLog =
                    evaluationLogRepository.findByRequestIdAndEvaluationStatusIn(evaluationResponse.getRequestId(),
                            Collections.singletonList(RequestStatus.FINISHED));
            if (evaluationLog != null) {
                EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
                requestEntity.setEvaluationLog(evaluationLog);
                ersRequestService.saveEvaluationResults(evaluationResponse.getEvaluationResults(), requestEntity);
            }
        });
    }
}
