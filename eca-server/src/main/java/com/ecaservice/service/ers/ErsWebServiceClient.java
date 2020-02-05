package com.ecaservice.service.ers;

import com.ecaservice.config.ws.ers.ErsConfig;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.GetEvaluationResultsRequest;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.mapping.EvaluationResultsMapper;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Service for communication with evaluation results web - service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErsWebServiceClient {

    private final WebServiceTemplate ersWebServiceTemplate;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final ErsConfig ersConfig;

    /**
     * Sends classifier evaluation results to  evaluation results web service.
     *
     * @param evaluationResults - evaluation results
     * @return evaluation results response
     */
    public EvaluationResultsResponse sendEvaluationResults(EvaluationResults evaluationResults, String requestId) {
        EvaluationResultsRequest resultsRequest = evaluationResultsMapper.map(evaluationResults);
        resultsRequest.setRequestId(requestId);
        log.trace("Starting to send evaluation results request '{}' to: {}.", requestId, ersConfig.getUrl());
        EvaluationResultsResponse resultsResponse =
                (EvaluationResultsResponse) ersWebServiceTemplate.marshalSendAndReceive(ersConfig.getUrl(),
                        resultsRequest);
        log.trace("Received response with requestId = {}, status = {} from {}.", resultsResponse.getRequestId(),
                resultsResponse.getStatus(), ersConfig.getUrl());
        return resultsResponse;
    }

    /**
     * Gets evaluation results response.
     *
     * @param request - evaluation results request
     * @return evaluation results response
     */
    public GetEvaluationResultsResponse getEvaluationResultsSimpleResponse(GetEvaluationResultsRequest request) {
        return (GetEvaluationResultsResponse) ersWebServiceTemplate.marshalSendAndReceive(ersConfig.getUrl(), request);
    }

    /**
     * Gets optimal classifier options for specified training data.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    public ClassifierOptionsResponse getClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        return (ClassifierOptionsResponse) ersWebServiceTemplate.marshalSendAndReceive(ersConfig.getUrl(),
                classifierOptionsRequest);
    }
}
