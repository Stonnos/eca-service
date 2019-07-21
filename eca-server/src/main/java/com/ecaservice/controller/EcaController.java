package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.exception.ExperimentException;
import com.ecaservice.mapping.EcaResponseMapper;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.async.AsyncTaskService;
import com.ecaservice.service.ers.ErsRequestService;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collections;

import static com.ecaservice.util.Utils.buildErrorResponse;

/**
 * Implements REST API for ECA application.
 *
 * @author Roman Batygin
 */
@Api(tags = "API for ECA application")
@Slf4j
@RestController
@RequestMapping("/eca-api")
public class EcaController {

    private final ExperimentRequestService experimentRequestService;
    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationOptimizerService evaluationOptimizerService;
    private final ErsRequestService ersRequestService;
    private final AsyncTaskService asyncTaskService;
    private final EcaResponseMapper ecaResponseMapper;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentRequestService   - experiment request service bean
     * @param evaluationRequestService   - evaluation request service bean
     * @param evaluationOptimizerService - evaluation optimizer service bean
     * @param ersRequestService          - ers request service bean
     * @param asyncTaskService           - async task service bean
     * @param ecaResponseMapper          - eca response mapper bean
     * @param evaluationLogRepository    - evaluation log repository bean
     */
    @Inject
    public EcaController(ExperimentRequestService experimentRequestService,
                         EvaluationRequestService evaluationRequestService,
                         EvaluationOptimizerService evaluationOptimizerService,
                         ErsRequestService ersRequestService,
                         AsyncTaskService asyncTaskService,
                         EcaResponseMapper ecaResponseMapper,
                         EvaluationLogRepository evaluationLogRepository) {
        this.experimentRequestService = experimentRequestService;
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationOptimizerService = evaluationOptimizerService;
        this.ersRequestService = ersRequestService;
        this.asyncTaskService = asyncTaskService;
        this.ecaResponseMapper = ecaResponseMapper;
        this.evaluationLogRepository = evaluationLogRepository;
    }

    /**
     * Creates experiment request.
     *
     * @param experimentRequest - experiment request dto
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('eca')")
    @ApiOperation(
            value = "Creates experiment request",
            notes = "Creates experiment request"
    )
    @PostMapping(value = "/experiment/create")
    public EcaResponse createRequest(@RequestBody @Valid ExperimentRequest experimentRequest) {
        Experiment experiment = experimentRequestService.createExperimentRequest(experimentRequest);
        return ecaResponseMapper.map(experiment);
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param evaluationRequest - evaluation request dto
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('eca')")
    @ApiOperation(
            value = "Evaluates classifier using specified evaluation method",
            notes = "Evaluates classifier using specified evaluation method"
    )
    @PostMapping(value = "/evaluation/execute")
    public EvaluationResponse execute(@RequestBody @Valid EvaluationRequest evaluationRequest) {
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(evaluationRequest);
        log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                evaluationResponse.getStatus());
        sendEvaluationResultsToErs(evaluationResponse);
        return evaluationResponse;
    }


    @PreAuthorize("#oauth2.hasScope('eca')")
    @GetMapping(value = "/hello")
    public String hello() {
        return "4343";
    }

    /**
     * Evaluates classifier using optimal options.
     *
     * @param instancesRequest - instances request
     * @return response entity
     */
    @PreAuthorize("#oauth2.hasScope('eca')")
    @ApiOperation(
            value = "Evaluates classifier using optimal options",
            notes = "Evaluates classifier using optimal options"
    )
    @PostMapping(value = "/evaluation/optimize")
    public EvaluationResponse optimize(@RequestBody @Valid InstancesRequest instancesRequest) {
        EvaluationResponse evaluationResponse =
                evaluationOptimizerService.evaluateWithOptimalClassifierOptions(instancesRequest);
        log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                evaluationResponse.getStatus());
        sendEvaluationResultsToErs(evaluationResponse);
        return evaluationResponse;
    }

    /**
     * Handles experiments error.
     *
     * @param ex - experiment exception
     * @return response entity
     */
    @ExceptionHandler(ExperimentException.class)
    public ResponseEntity<EcaResponse> handleError(ExperimentException ex) {
        return ResponseEntity.ok(buildErrorResponse(ex.getMessage()));
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
