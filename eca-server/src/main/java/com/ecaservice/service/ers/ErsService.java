package com.ecaservice.service.ers;

import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.mapping.GetEvaluationResultsMapper;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.converters.model.ExperimentHistory;
import eca.core.evaluation.EvaluationResults;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    /**
     * Sent experiment results to ERS service.
     *
     * @param experimentResultsEntity - experiment entity
     * @param experimentHistory       - experiment history
     * @param source                  - experiment results request source
     */
    public void sentExperimentResults(ExperimentResultsEntity experimentResultsEntity,
                                      ExperimentHistory experimentHistory, ExperimentResultsRequestSource source) {
        ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
        experimentResultsRequest.setRequestSource(source);
        experimentResultsRequest.setExperimentResults(experimentResultsEntity);
        EvaluationResults evaluationResults =
                experimentHistory.getExperiment().get(experimentResultsEntity.getResultsIndex());
        ersRequestService.saveEvaluationResults(evaluationResults, experimentResultsRequest);
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
        } catch (FeignException.ServiceUnavailable ex) {
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
