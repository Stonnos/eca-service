package com.ecaservice.service.evaluation;

import com.ecaservice.config.EvaluationResultsServiceConfig;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.EvaluationResultsMapper;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.repository.EvaluationResultsRequestRepository;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for communication with evaluation results web - service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationResultsSender {

    private final WebServiceTemplate webServiceTemplate;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final EvaluationResultsServiceConfig serviceConfig;
    private final EvaluationResultsRequestRepository evaluationResultsRequestRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param webServiceTemplate                 - web service template bean
     * @param evaluationResultsMapper            - evaluation results mapper bean
     * @param serviceConfig                      - evaluation results service config bean
     * @param evaluationResultsRequestRepository - evaluation results request repository bean
     */
    @Inject
    public EvaluationResultsSender(WebServiceTemplate webServiceTemplate,
                                   EvaluationResultsMapper evaluationResultsMapper,
                                   EvaluationResultsServiceConfig serviceConfig,
                                   EvaluationResultsRequestRepository evaluationResultsRequestRepository) {
        this.webServiceTemplate = webServiceTemplate;
        this.evaluationResultsMapper = evaluationResultsMapper;
        this.serviceConfig = serviceConfig;
        this.evaluationResultsRequestRepository = evaluationResultsRequestRepository;
    }

    /**
     * Sends classifier evaluation results to  evaluation results web service
     *
     * @param evaluationResults - evaluation results
     * @param evaluationLog     - evaluation log
     */
    @Async("evaluationResultsThreadPoolTaskExecutor")
    public void sendEvaluationResults(EvaluationResults evaluationResults, EvaluationLog evaluationLog) {
        if (!Boolean.TRUE.equals(serviceConfig.getEnabled())) {
            log.info("Evaluation results sending to url '{}' is disabled.", serviceConfig.getUrl());
        } else {
            String requestId = UUID.randomUUID().toString();
            log.info("Starting to send evaluation results request '{}' to: {}.", requestId, serviceConfig.getUrl());
            EvaluationResultsRequestEntity requestEntity =
                    createEvaluationResultsRequestEntity(requestId, evaluationLog);
            try {
                EvaluationResultsRequest resultsRequest = evaluationResultsMapper.map(evaluationResults);
                resultsRequest.setRequestId(requestId);
                EvaluationResultsResponse resultsResponse =
                        (EvaluationResultsResponse) webServiceTemplate.marshalSendAndReceive(serviceConfig.getUrl(),
                                resultsRequest);
                log.info("Received response with id = {}, status = {} from {}.", resultsResponse.getRequestId(),
                        resultsResponse.getStatus(), serviceConfig.getUrl());
                requestEntity.setResponseStatus(resultsResponse.getStatus());
            } catch (Exception ex) {
                log.error("There was an error while sending evaluation results: {}", ex.getMessage());
                requestEntity.setResponseStatus(ResponseStatus.ERROR);
                requestEntity.setDetails(ex.getMessage());
            } finally {
                evaluationResultsRequestRepository.save(requestEntity);
            }
        }
    }

    private EvaluationResultsRequestEntity createEvaluationResultsRequestEntity(String requestId,
                                                                                EvaluationLog evaluationLog) {
        EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setRequestId(requestId);
        requestEntity.setRequestDate(LocalDateTime.now());
        requestEntity.setEvaluationLog(evaluationLog);
        return requestEntity;
    }
}
