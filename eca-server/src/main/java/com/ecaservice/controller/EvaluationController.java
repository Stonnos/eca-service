package com.ecaservice.controller;

import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.async.AsyncTaskService;
import com.ecaservice.service.ers.ErsRequestService;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.util.Utils.buildErrorResponse;

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
    private final AsyncTaskService asyncTaskService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Constructor with dependency spring injection.
     *
     * @param evaluationRequestService   - evaluation request service bean
     * @param ersRequestService          - ers request service bean
     * @param evaluationOptimizerService - evaluation optimizer service bean
     * @param asyncTaskService           - async task service bean
     * @param evaluationLogRepository    - evaluation log repository bean
     */
    @Inject
    public EvaluationController(EvaluationRequestService evaluationRequestService,
                                ErsRequestService ersRequestService,
                                EvaluationOptimizerService evaluationOptimizerService,
                                AsyncTaskService asyncTaskService,
                                EvaluationLogRepository evaluationLogRepository) {
        this.evaluationRequestService = evaluationRequestService;
        this.ersRequestService = ersRequestService;
        this.evaluationOptimizerService = evaluationOptimizerService;
        this.asyncTaskService = asyncTaskService;
        this.evaluationLogRepository = evaluationLogRepository;
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
        log.info("Evaluation response [{}] with status [{}] has been built.", evaluationResponse.getRequestId(),
                evaluationResponse.getStatus());
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
            return ResponseEntity.ok(evaluationResponse);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.badRequest().body(buildErrorResponse(ex.getMessage()));
        }
    }
}
