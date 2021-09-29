package com.ecaservice.ers.service;

import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.exception.DuplicateRequestIdException;
import com.ecaservice.ers.exception.ResultsNotFoundException;
import com.ecaservice.ers.mapping.EvaluationResultsMapper;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.ers.repository.EvaluationResultsInfoRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import static com.ecaservice.ers.config.MetricConstants.GET_EVALUATION_RESULTS_TIMED_METRIC_NAME;
import static com.ecaservice.ers.config.MetricConstants.SAVE_EVALUATION_RESULTS_TIMED_METRIC_NAME;

/**
 * Implements service for saving evaluation results into database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResultsService {

    private final InstancesService instancesService;
    private final EvaluationResultsMapper evaluationResultsMapper;
    private final EvaluationResultsInfoRepository evaluationResultsInfoRepository;

    private ConcurrentHashMap<String, Object> cachedRequestIds = new ConcurrentHashMap<>();

    /**
     * Saves evaluation results report into database.
     *
     * @param evaluationResultsRequest - evaluation results report
     * @return evaluation results response
     */
    @Timed(value = SAVE_EVALUATION_RESULTS_TIMED_METRIC_NAME)
    public EvaluationResultsResponse saveEvaluationResults(EvaluationResultsRequest evaluationResultsRequest) {
        log.info("Starting to save evaluation results report with request id = {}.",
                evaluationResultsRequest.getRequestId());
        cachedRequestIds.putIfAbsent(evaluationResultsRequest.getRequestId(), new Object());
        synchronized (cachedRequestIds.get(evaluationResultsRequest.getRequestId())) {
            if (evaluationResultsInfoRepository.existsByRequestId(evaluationResultsRequest.getRequestId())) {
                log.error("Evaluation results with request id = {} is already exists!",
                        evaluationResultsRequest.getRequestId());
                throw new DuplicateRequestIdException(evaluationResultsRequest.getRequestId());
            } else {
                InstancesInfo instancesInfo = instancesService.getOrSaveInstancesInfo(evaluationResultsRequest);
                EvaluationResultsInfo evaluationResultsInfo =
                        evaluationResultsMapper.map(evaluationResultsRequest);
                evaluationResultsInfo.setInstances(instancesInfo);
                evaluationResultsInfo.setSaveDate(LocalDateTime.now());
                evaluationResultsInfoRepository.save(evaluationResultsInfo);
                log.info("Evaluation results report with request id = {} has been successfully saved.",
                        evaluationResultsRequest.getRequestId());
            }
        }
        cachedRequestIds.remove(evaluationResultsRequest.getRequestId());
        return EvaluationResultsResponse.builder()
                .requestId(evaluationResultsRequest.getRequestId())
                .build();
    }

    /**
     * Gets evaluation results response.
     *
     * @param request - evaluation results request
     * @return evaluation results simple response
     */
    @Timed(value = GET_EVALUATION_RESULTS_TIMED_METRIC_NAME)
    @Transactional
    public GetEvaluationResultsResponse getEvaluationResultsResponse(GetEvaluationResultsRequest request) {
        log.info("Starting to get evaluation results for request id [{}]", request.getRequestId());
        var evaluationResultsInfo = evaluationResultsInfoRepository.findByRequestId(request.getRequestId());
        if (evaluationResultsInfo == null) {
            throw new ResultsNotFoundException(String.format("Evaluation results not found for request id [%s]",
                    request.getRequestId()));
        } else {
            var response = evaluationResultsMapper.map(evaluationResultsInfo);
            log.info("Received evaluation results for request id [{}]", request.getRequestId());
            return response;
        }
    }
}
