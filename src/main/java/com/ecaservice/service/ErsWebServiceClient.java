package com.ecaservice.service;

import com.ecaservice.config.EvaluationResultsServiceConfig;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.exception.EcaServiceException;
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

    private static final String SERVICE_DISABLED_MESSAGE_FORMAT = "Evaluation results sending to url '%s' is disabled.";

    private final WebServiceTemplate webServiceTemplate;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final EvaluationResultsServiceConfig serviceConfig;

    /**
     * Constructor with spring dependency injection.
     *
     * @param webServiceTemplate      - web service template bean
     * @param evaluationResultsMapper - evaluation results mapper bean
     * @param serviceConfig           - evaluation results service config bean
     */
    @Inject
    public ErsWebServiceClient(WebServiceTemplate webServiceTemplate,
                               EvaluationResultsMapper evaluationResultsMapper,
                               EvaluationResultsServiceConfig serviceConfig) {
        this.webServiceTemplate = webServiceTemplate;
        this.evaluationResultsMapper = evaluationResultsMapper;
        this.serviceConfig = serviceConfig;
    }

    /**
     * Sends classifier evaluation results to  evaluation results web service.
     *
     * @param evaluationResults - evaluation results
     * @return evaluation results response
     */
    public EvaluationResultsResponse sendEvaluationResults(EvaluationResults evaluationResults, String requestId) {
        if (!Boolean.TRUE.equals(serviceConfig.getEnabled())) {
            log.trace(String.format(SERVICE_DISABLED_MESSAGE_FORMAT, serviceConfig.getUrl()));
            throw new EcaServiceException(String.format(SERVICE_DISABLED_MESSAGE_FORMAT, serviceConfig.getUrl()));
        } else {
            EvaluationResultsRequest resultsRequest = evaluationResultsMapper.map(evaluationResults);
            resultsRequest.setRequestId(requestId);
            log.trace("Starting to send evaluation results request '{}' to: {}.", requestId, serviceConfig.getUrl());
            EvaluationResultsResponse resultsResponse =
                    (EvaluationResultsResponse) webServiceTemplate.marshalSendAndReceive(serviceConfig.getUrl(),
                            resultsRequest);
            log.trace("Received response with requestId = {}, status = {} from {}.", resultsResponse.getRequestId(),
                    resultsResponse.getStatus(), serviceConfig.getUrl());
            return resultsResponse;
        }
    }

    /**
     * Gets optimal classifier options for specified training data.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    public ClassifierOptionsResponse getClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        return (ClassifierOptionsResponse) webServiceTemplate.marshalSendAndReceive(serviceConfig.getUrl(),
                classifierOptionsRequest);
    }
}
