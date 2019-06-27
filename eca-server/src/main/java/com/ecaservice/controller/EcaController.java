package com.ecaservice.controller;

import com.ecaservice.dto.EcaResponse;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.async.AsyncTaskService;
import com.ecaservice.service.ers.ErsRequestService;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.util.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;

import static com.ecaservice.util.Utils.buildErrorResponse;
import static com.ecaservice.util.Utils.existsFile;

/**
 * Implements REST API for ECA application.
 *
 * @author Roman Batygin
 */
@Api(tags = "Operations for ECA application")
@Slf4j
@RestController
@RequestMapping("/eca/api")
public class EcaController {

    private final ExperimentService experimentService;
    private final ExperimentRequestService experimentRequestService;
    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationOptimizerService evaluationOptimizerService;
    private final ErsRequestService ersRequestService;
    private final AsyncTaskService asyncTaskService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param experimentService          - experiment service bean
     * @param experimentRequestService   - experiment request service bean
     * @param evaluationRequestService   - evaluation request service bean
     * @param evaluationOptimizerService - evaluation optimizer service bean
     * @param ersRequestService          - ers request service bean
     * @param asyncTaskService           - async task service bean
     * @param evaluationLogRepository    - evaluation log repository bean
     */
    @Inject
    public EcaController(ExperimentService experimentService,
                         ExperimentRequestService experimentRequestService,
                         EvaluationRequestService evaluationRequestService,
                         EvaluationOptimizerService evaluationOptimizerService,
                         ErsRequestService ersRequestService,
                         AsyncTaskService asyncTaskService,
                         EvaluationLogRepository evaluationLogRepository) {
        this.experimentService = experimentService;
        this.experimentRequestService = experimentRequestService;
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationOptimizerService = evaluationOptimizerService;
        this.ersRequestService = ersRequestService;
        this.asyncTaskService = asyncTaskService;
        this.evaluationLogRepository = evaluationLogRepository;
    }

    /**
     * Downloads experiment by specified uuid.
     *
     * @param uuid - experiment uuid
     */
    @ApiOperation(
            value = "Downloads experiment by specified uuid",
            notes = "Downloads experiment by specified uuid"
    )
    @GetMapping(value = "/experiment/download/{uuid}")
    public ResponseEntity downloadExperiment(
            @ApiParam(value = "Experiment uuid", required = true) @PathVariable String uuid) {
        File experimentFile = experimentService.findExperimentFileByUuid(uuid);
        if (!existsFile(experimentFile)) {
            log.error("Experiment results file for uuid = '{}' not found!", uuid);
            return ResponseEntity.badRequest().body(
                    String.format("Experiment results file for uuid = '%s' not found!", uuid));
        }
        log.info("Download experiment file '{}' for uuid = '{}'", experimentFile.getAbsolutePath(), uuid);
        return Utils.buildAttachmentResponse(experimentFile);
    }

    /**
     * Creates experiment request.
     *
     * @param experimentRequest - experiment request dto
     * @return response entity
     */
    @ApiOperation(
            value = "Creates experiment request",
            notes = "Creates experiment request"
    )
    @PostMapping(value = "/experiment/create")
    public ResponseEntity<EcaResponse> createRequest(@RequestBody ExperimentRequest experimentRequest) {
        EcaResponse ecaResponse = experimentRequestService.createExperimentRequest(experimentRequest);
        return ResponseEntity.ok(ecaResponse);
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
    @PostMapping(value = "/evaluation/execute")
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
    @PostMapping(value = "/evaluation/optimize")
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
