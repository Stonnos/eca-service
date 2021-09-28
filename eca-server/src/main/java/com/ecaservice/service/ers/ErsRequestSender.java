package com.ecaservice.service.ers;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.service.evaluation.EvaluationResultsService;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for communication with evaluation results web - service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErsRequestSender {

    private final EvaluationResultsService evaluationResultsService;
    private final ErsClient ersClient;

    /**
     * Sends classifier evaluation results to  evaluation results web service.
     *
     * @param evaluationResults - evaluation results
     * @return evaluation results response
     */
    public EvaluationResultsResponse sendEvaluationResults(EvaluationResults evaluationResults, String requestId) {
        EvaluationResultsRequest evaluationResultsRequest = evaluationResultsService.proceed(evaluationResults);
        evaluationResultsRequest.setRequestId(requestId);
        log.trace("Starting to send evaluation results request [{}] to ERS", requestId);
        EvaluationResultsResponse resultsResponse = ersClient.save(evaluationResultsRequest);
        log.trace("Received response from ERS: requestId [{}] ", resultsResponse.getRequestId());
        return resultsResponse;
    }

    /**
     * Gets evaluation results response.
     *
     * @param request - evaluation results request
     * @return evaluation results response
     */
    public GetEvaluationResultsResponse getEvaluationResultsSimpleResponse(GetEvaluationResultsRequest request) {
        log.trace("Gets evaluation results report [{}]", request.getRequestId());
        return ersClient.getEvaluationResults(request);
    }

    /**
     * Gets optimal classifier options for specified training data.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    public ClassifierOptionsResponse getClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        log.trace("Gets optimal classifier options for data [{}]", classifierOptionsRequest.getRelationName());
        return ersClient.getClassifierOptions(classifierOptionsRequest);
    }
}
