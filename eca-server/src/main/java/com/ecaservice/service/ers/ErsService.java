package com.ecaservice.service.ers;

import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.mapping.GetEvaluationResultsMapper;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.repository.ExperimentResultsRequestRepository;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.WebServiceIOException;

import java.util.concurrent.ConcurrentHashMap;

import static com.ecaservice.util.Utils.buildEvaluationResultsDto;

/**
 * ERS service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErsService {

    private final ErsRequestService ersRequestService;
    private final GetEvaluationResultsMapper evaluationResultsMapper;
    private final ExperimentResultsRequestRepository experimentResultsRequestRepository;

    private final ConcurrentHashMap<Long, Object> resultsIdsMap = new ConcurrentHashMap<>();

    /**
     * Sent experiment results to ERS service.
     *
     * @param experimentResultsEntity - experiment entity
     * @param experimentHistory       - experiment history
     * @param source                  - experiment results request source
     */
    public void sentExperimentResults(ExperimentResultsEntity experimentResultsEntity,
                                      ExperimentHistory experimentHistory, ExperimentResultsRequestSource source) {
        resultsIdsMap.putIfAbsent(experimentResultsEntity.getId(), new Object());
        synchronized (resultsIdsMap.get(experimentResultsEntity.getId())) {
            if (experimentResultsRequestRepository.existsByExperimentResultsAndResponseStatusEquals(
                    experimentResultsEntity, ErsResponseStatus.SUCCESS)) {
                log.warn("Experiment results [{}] is already sent to ERS", experimentResultsEntity.getId());
            } else {
                ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
                experimentResultsRequest.setRequestSource(source);
                experimentResultsRequest.setExperimentResults(experimentResultsEntity);
                EvaluationResults evaluationResults =
                        experimentHistory.getExperiment().get(experimentResultsEntity.getResultsIndex());
                ersRequestService.saveEvaluationResults(evaluationResults, experimentResultsRequest);
            }
        }
        resultsIdsMap.remove(experimentResultsEntity.getId());
    }

    /**
     * Gets evaluation results from ERS service.
     *
     * @param requestId - request id
     * @return evaluation results dto
     */
    public EvaluationResultsDto getEvaluationResultsFromErs(String requestId) {
        EvaluationResultsStatus evaluationResultsStatus;
        try {
            GetEvaluationResultsResponse evaluationResultsResponse = ersRequestService.getEvaluationResults(requestId);
            return evaluationResultsMapper.map(evaluationResultsResponse);
        } catch (WebServiceIOException ex) {
            log.error(ex.getMessage());
            evaluationResultsStatus = EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE;
        } catch (Exception ex) {
            log.error("There was an error while fetching evaluation results for request id [{}]: {}", requestId,
                    ex.getMessage());
            evaluationResultsStatus = EvaluationResultsStatus.ERROR;
        }
        return buildEvaluationResultsDto(evaluationResultsStatus);
    }
}