package com.ecaservice.service;

import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationResultsRequestEntity;
import com.ecaservice.repository.EvaluationResultsRequestRepository;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * Implements service for saving evaluation results by sending request to ERS web - service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationResultsService {

    private final EvaluationResultsSender evaluationResultsSender;
    private final EvaluationResultsRequestRepository evaluationResultsRequestRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param evaluationResultsSender            - evaluation results sender bean
     * @param evaluationResultsRequestRepository - evaluation results request repository bean
     */
    @Inject
    public EvaluationResultsService(EvaluationResultsSender evaluationResultsSender,
                                    EvaluationResultsRequestRepository evaluationResultsRequestRepository) {
        this.evaluationResultsSender = evaluationResultsSender;
        this.evaluationResultsRequestRepository = evaluationResultsRequestRepository;
    }

    /**
     * Save evaluation results by sending request to ERS web - service.
     *
     * @param evaluationResults - evaluation results
     * @param evaluationLog     - evaluation log
     */
    @Async("evaluationResultsThreadPoolTaskExecutor")
    public void saveEvaluationResults(EvaluationResults evaluationResults, EvaluationLog evaluationLog) {
        EvaluationResultsRequestEntity requestEntity = new EvaluationResultsRequestEntity();
        requestEntity.setRequestDate(LocalDateTime.now());
        requestEntity.setEvaluationLog(evaluationLog);
        try {
            EvaluationResultsResponse resultsResponse =
                    evaluationResultsSender.sendEvaluationResults(evaluationResults);
            requestEntity.setRequestId(resultsResponse.getRequestId());
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
