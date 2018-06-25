package com.ecaservice.service.ers;

import com.ecaservice.config.ErsConfig;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.mapping.EvaluationResultsMapper;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.inject.Inject;

/**
 * Service for communication with evaluation results web - service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ErsWebServiceClient {

    private final WebServiceTemplate webServiceTemplate;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final ErsConfig ersConfig;

    /**
     * Constructor with spring dependency injection.
     *
     * @param webServiceTemplate      - web service template bean
     * @param evaluationResultsMapper - evaluation results mapper bean
     * @param ersConfig               - evaluation results service config bean
     */
    @Inject
    public ErsWebServiceClient(WebServiceTemplate webServiceTemplate,
                               EvaluationResultsMapper evaluationResultsMapper,
                               ErsConfig ersConfig) {
        this.webServiceTemplate = webServiceTemplate;
        this.evaluationResultsMapper = evaluationResultsMapper;
        this.ersConfig = ersConfig;
    }

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
                (EvaluationResultsResponse) webServiceTemplate.marshalSendAndReceive(ersConfig.getUrl(),
                        resultsRequest);
        log.trace("Received response with requestId = {}, status = {} from {}.", resultsResponse.getRequestId(),
                resultsResponse.getStatus(), ersConfig.getUrl());
        return resultsResponse;
    }

    /**
     * Gets optimal classifier options for specified training data.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    public ClassifierOptionsResponse getClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        return (ClassifierOptionsResponse) webServiceTemplate.marshalSendAndReceive(ersConfig.getUrl(),
                classifierOptionsRequest);
    }
}
