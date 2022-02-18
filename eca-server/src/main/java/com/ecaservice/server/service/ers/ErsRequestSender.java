package com.ecaservice.server.service.ers;

import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Ers request sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Retryable
@Service
@RequiredArgsConstructor
public class ErsRequestSender {

    private final ErsClient ersClient;

    /**
     * Saves evaluation results report.
     *
     * @param evaluationResultsRequest - evaluation results request
     * @return evaluation results response
     */
    @Retry(value = "ersEvaluationResultsRequest", requestIdKey = "#evaluationResultsRequest.requestId",
            exceptionStrategy = "feignExceptionStrategy", retryCallback = "ersRetryCallback")
    public EvaluationResultsResponse save(EvaluationResultsRequest evaluationResultsRequest) {
        log.info("Starting to send evaluation results to ERS with request [{}]",
                evaluationResultsRequest.getRequestId());
        var evaluationResultsResponse = ersClient.save(evaluationResultsRequest);
        log.info("Received success response for requestId [{}] from ERS.", evaluationResultsResponse.getRequestId());
        return evaluationResultsResponse;
    }
}
