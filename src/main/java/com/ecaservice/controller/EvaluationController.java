package com.ecaservice.controller;

import com.ecaservice.dto.EvaluationRequestDto;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.mapping.EvaluationRequestMapper;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.model.evaluation.EvaluationRequest;
import com.ecaservice.model.evaluation.EvaluationStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.ers.ErsRequestService;
import com.ecaservice.service.evaluation.EvaluationOptimizerService;
import com.ecaservice.service.evaluation.EvaluationRequestService;
import com.ecaservice.util.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

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
    private final EvaluationRequestMapper evaluationRequestMapper;
    private final ErsRequestService ersRequestService;
    private final EvaluationOptimizerService evaluationOptimizerService;
    private final EvaluationLogRepository evaluationLogRepository;

    /**
     * Constructor with dependency spring injection.
     *
     * @param evaluationRequestService   - evaluation request service bean
     * @param evaluationRequestMapper    - evaluation request mapper bean
     * @param ersRequestService          - ers request service bean
     * @param evaluationOptimizerService - evaluation optimizer service bean
     * @param evaluationLogRepository    - evaluation log repository bean
     */
    @Inject
    public EvaluationController(EvaluationRequestService evaluationRequestService,
                                EvaluationRequestMapper evaluationRequestMapper,
                                ErsRequestService ersRequestService,
                                EvaluationOptimizerService evaluationOptimizerService,
                                EvaluationLogRepository evaluationLogRepository) {
        this.evaluationRequestService = evaluationRequestService;
        this.evaluationRequestMapper = evaluationRequestMapper;
        this.ersRequestService = ersRequestService;
        this.evaluationOptimizerService = evaluationOptimizerService;
        this.evaluationLogRepository = evaluationLogRepository;
    }

    /**
     * Processed the request on classifier model evaluation.
     *
     * @param evaluationRequestDto - evaluation request dto
     * @param request              - http servlet request
     * @return response entity
     */
    @ApiOperation(
            value = "Evaluates classifier using specified evaluation method",
            notes = "Evaluates classifier using specified evaluation method"
    )
    @PostMapping(value = "/execute")
    public ResponseEntity<EvaluationResponse> execute(@RequestBody EvaluationRequestDto evaluationRequestDto,
                                                      HttpServletRequest request) {
        log.info("Received evaluation request for client {}", request.getRemoteAddr());
        EvaluationRequest evaluationRequest = evaluationRequestMapper.map(evaluationRequestDto);
        evaluationRequest.setIpAddress(request.getRemoteAddr());
        EvaluationResponse evaluationResponse = evaluationRequestService.processRequest(evaluationRequest);
        EvaluationLog evaluationLog =
                evaluationLogRepository.findByRequestIdAndEvaluationStatusIn(evaluationResponse.getRequestId(),
                        Collections.singletonList(EvaluationStatus.FINISHED));
        if (evaluationLog != null) {
            EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
            requestEntity.setEvaluationLog(evaluationLog);
            ersRequestService.saveEvaluationResults(evaluationResponse.getEvaluationResults(), requestEntity);
        }
        log.info("Evaluation response with status [{}] has been built.", evaluationResponse.getStatus());
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
            log.info("Evaluation response with status [{}] has been built.", evaluationResponse.getStatus());
            return ResponseEntity.ok(evaluationResponse);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Utils.buildErrorResponse(ex.getMessage()));
        }
    }

}
