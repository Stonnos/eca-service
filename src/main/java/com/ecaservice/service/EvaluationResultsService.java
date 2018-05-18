package com.ecaservice.service;

import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.repository.ErsRequestRepository;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implements service for saving evaluation results by sending request to ERS web - service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationResultsService {

    private final EvaluationResultsSender evaluationResultsSender;
    private final ErsRequestRepository ersRequestRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param evaluationResultsSender - evaluation results sender bean
     * @param ersRequestRepository    - evaluation results service request repository bean
     */
    @Inject
    public EvaluationResultsService(EvaluationResultsSender evaluationResultsSender,
                                    ErsRequestRepository ersRequestRepository) {
        this.evaluationResultsSender = evaluationResultsSender;
        this.ersRequestRepository = ersRequestRepository;
    }

    /**
     * Save evaluation results by sending request to ERS web - service.
     *
     * @param evaluationResults - evaluation results
     * @param ersRequest        - evaluation results service request
     */
    @Async("evaluationResultsThreadPoolTaskExecutor")
    public void saveEvaluationResults(EvaluationResults evaluationResults, ErsRequest ersRequest) {
        ersRequest.setRequestDate(LocalDateTime.now());
        ersRequest.setRequestId(UUID.randomUUID().toString());
        try {
            EvaluationResultsResponse resultsResponse =
                    evaluationResultsSender.sendEvaluationResults(evaluationResults, ersRequest.getRequestId());
            ersRequest.setResponseStatus(resultsResponse.getStatus());
        } catch (Exception ex) {
            log.error("There was an error while sending evaluation results: {}", ex.getMessage());
            ersRequest.setResponseStatus(ResponseStatus.ERROR);
            ersRequest.setDetails(ex.getMessage());
        } finally {
            ersRequestRepository.save(ersRequest);
        }
    }
}
