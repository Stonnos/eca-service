package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.ecaserver.ErsExperimentResultsRequest;
import com.ecaservice.auto.test.entity.ecaserver.Experiment;
import com.ecaservice.auto.test.entity.ecaserver.ExperimentResultsEntity;
import com.ecaservice.auto.test.exception.ErsExperimentResultsRequestsNotFoundException;
import com.ecaservice.auto.test.exception.ExperimentResultsNotFoundException;
import com.ecaservice.auto.test.repository.ecaserver.ErsExperimentResultsRequestRepository;
import com.ecaservice.auto.test.repository.ecaserver.ExperimentRepository;
import com.ecaservice.auto.test.repository.ecaserver.ExperimentResultsRepository;
import com.ecaservice.auto.test.service.api.ErsClient;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Implements service for providing evaluation results from ERS.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErsService {

    private static final String GET_ERS_EXPERIMENT_RESULTS_LOG_MESSAGE =
            "Starting to fetch evaluation results [{}] from ERS for experiment [{}]. Index in experiment history: [{}]";
    public static final String FETCHED_ERS_EXPERIMENT_RESULTS_LOG_MESSAGE =
            "Evaluation results [{}] has been fetched from ERS for experiment [{}]. Index in experiment history: [{}]";
    private static final String SUCCESS = "SUCCESS";

    private final ErsClient ersClient;
    private final ExperimentRepository experimentRepository;
    private final ExperimentResultsRepository experimentResultsRepository;
    private final ErsExperimentResultsRequestRepository ersExperimentResultsRequestRepository;

    /**
     * Gets experiment evaluation results from ERS.
     *
     * @param requestId - experiment request id from eca - server
     * @return experiment evaluation results map
     */
    public Map<Integer, GetEvaluationResultsResponse> getExperimentEvaluationResults(String requestId) {
        log.info("Starting to get ERS evaluation results for experiment [{}]", requestId);
        var experiment = experimentRepository.findByRequestId(requestId)
                .orElseThrow(() -> new EntityNotFoundException(Experiment.class, requestId));
        var experimentResults = experimentResultsRepository.findByExperimentOrderByResultsIndex(experiment);
        if (CollectionUtils.isEmpty(experimentResults)) {
            throw new ExperimentResultsNotFoundException(
                    String.format("Got empty experiment [%s] results from db", requestId));
        }
        log.info("Got [{}] experiment [{}] results from eca - server db", experimentResults.size(), requestId);
        var ersExperimentResultsRequests = getErsExperimentResultsRequests(experimentResults, requestId);
        var evaluationResultsMap = getExperimentResultsFromErs(ersExperimentResultsRequests);
        log.info("Got [{}] evaluation results from ERS for experiment [{}]", evaluationResultsMap.size(), requestId);
        return evaluationResultsMap;
    }

    private List<ErsExperimentResultsRequest> getErsExperimentResultsRequests(
            List<ExperimentResultsEntity> experimentResults, String experimentRequestId) {
        var ersExperimentResultsRequests =
                ersExperimentResultsRequestRepository.findByExperimentResultsIn(experimentResults);
        var ersExperimentResultsMap = ersExperimentResultsRequests
                .stream()
                .collect(Collectors.toMap(er -> er.getExperimentResults().getId(), Function.identity()));
        var notFoundIds = experimentResults
                .stream()
                .filter(experimentResultsEntity -> !ersExperimentResultsMap.containsKey(
                        experimentResultsEntity.getId()))
                .map(ExperimentResultsEntity::getId)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(notFoundIds)) {
            String errorMessage =
                    String.format("Ers experiment [%s] results requests not found for experiment results ids: %s",
                            experimentRequestId, notFoundIds);
            throw new ErsExperimentResultsRequestsNotFoundException(errorMessage);
        }
        var errorRequestIds = ersExperimentResultsRequests.stream()
                .filter(ersExperimentResultsRequest -> !SUCCESS.equals(
                        ersExperimentResultsRequest.getResponseStatus()))
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(errorRequestIds)) {
            String errorMessage = String.format("Found ers experiment [%s] results requests ids with error status: %s",
                    experimentRequestId, errorRequestIds);
            throw new ErsExperimentResultsRequestsNotFoundException(errorMessage);
        }
        return ersExperimentResultsRequests;
    }

    private Map<Integer, GetEvaluationResultsResponse> getExperimentResultsFromErs(
            List<ErsExperimentResultsRequest> ersExperimentResultsRequests) {
        Map<Integer, GetEvaluationResultsResponse> evaluationResults = newHashMap();
        ersExperimentResultsRequests.forEach(ersExperimentResultsRequest -> {
            var experimentResults = ersExperimentResultsRequest.getExperimentResults();
            var experiment = experimentResults.getExperiment();
            log.info(GET_ERS_EXPERIMENT_RESULTS_LOG_MESSAGE, ersExperimentResultsRequest.getRequestId(),
                    experiment.getRequestId(), experimentResults.getResultsIndex());
            var evaluationResultRequest = new GetEvaluationResultsRequest(ersExperimentResultsRequest.getRequestId());
            var evaluationResultsResponse = ersClient.getEvaluationResults(evaluationResultRequest);
            evaluationResults.put(experimentResults.getResultsIndex(), evaluationResultsResponse);
            log.info(FETCHED_ERS_EXPERIMENT_RESULTS_LOG_MESSAGE, ersExperimentResultsRequest.getRequestId(),
                    experiment.getRequestId(), experimentResults.getResultsIndex());
        });
        return evaluationResults;
    }
}
