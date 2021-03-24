package com.ecaservice.ers.controller;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.service.ClassifierOptionsRequestService;
import com.ecaservice.ers.service.EvaluationResultsService;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.ecaservice.ers.config.MetricConstants.GET_EVALUATION_RESULTS_TIMED_METRIC_NAME;
import static com.ecaservice.ers.config.MetricConstants.GET_OPTIMAL_CLASSIFIER_OPTIONS_TIMED_METRIC_NAME;
import static com.ecaservice.ers.config.MetricConstants.SAVE_EVALUATION_RESULTS_TIMED_METRIC_NAME;

/**
 * Evaluation results service controller.
 *
 * @author Roman Batygin
 */
@Slf4j
@Api(tags = "Evaluation results storage API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EvaluationResultsController {

    private final EvaluationResultsService evaluationResultsService;
    private final ClassifierOptionsRequestService classifierOptionsRequestService;

    /**
     * Saves evaluation results report.
     *
     * @param evaluationResultsRequest - evaluation result request
     * @return evaluation results response
     */
    @Timed(value = SAVE_EVALUATION_RESULTS_TIMED_METRIC_NAME)
    @ApiOperation(
            value = "Saves evaluation results report",
            notes = "Saves evaluation results report"
    )
    @PostMapping(value = "/save")
    public EvaluationResultsResponse save(@Valid @RequestBody EvaluationResultsRequest evaluationResultsRequest) {
        log.info("Received request to save evaluation results report [{}]", evaluationResultsRequest.getRequestId());
        return evaluationResultsService.saveEvaluationResults(evaluationResultsRequest);
    }

    /**
     * Gets evaluation results simple report.
     *
     * @param request - get evaluation result request
     * @return evaluation results response
     */
    @Timed(value = GET_EVALUATION_RESULTS_TIMED_METRIC_NAME)
    @ApiOperation(
            value = "Gets evaluation results simple report",
            notes = "Gets evaluation results simple report"
    )
    @PostMapping(value = "/results")
    public GetEvaluationResultsResponse getEvaluationResultsResponse(
            @Valid @RequestBody GetEvaluationResultsRequest request) {
        log.info("Received request to get evaluation results report [{}]", request.getRequestId());
        return evaluationResultsService.getEvaluationResultsResponse(request);
    }

    /**
     * Endpoint for searching optimal classifiers options for specified request.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    @Timed(value = GET_OPTIMAL_CLASSIFIER_OPTIONS_TIMED_METRIC_NAME)
    @ApiOperation(
            value = "Endpoint for searching optimal classifiers options for specified request",
            notes = "Endpoint for searching optimal classifiers options for specified request"
    )
    @PostMapping(value = "/optimal-classifier-options")
    public ClassifierOptionsResponse findClassifierOptions(
            @Valid @RequestBody ClassifierOptionsRequest classifierOptionsRequest) {
        log.info("Received request to find optimal classifiers options for data [{}]",
                classifierOptionsRequest.getInstances().getRelationName());
        return classifierOptionsRequestService.findClassifierOptions(classifierOptionsRequest);
    }
}
