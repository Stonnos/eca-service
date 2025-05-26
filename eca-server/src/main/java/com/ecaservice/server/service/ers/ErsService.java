package com.ecaservice.server.service.ers;

import com.ecaservice.common.web.exception.InternalServiceUnavailableException;
import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.server.exception.ErsBadRequestException;
import com.ecaservice.server.mapping.GetEvaluationResultsMapper;
import com.ecaservice.server.model.ErsEvaluationRequestData;
import com.ecaservice.server.model.entity.ExperimentResultsEntity;
import com.ecaservice.server.model.entity.ExperimentResultsRequest;
import com.ecaservice.server.service.evaluation.ConfusionMatrixService;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.server.util.Utils.buildEvaluationResultsDto;

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
    private final ConfusionMatrixService confusionMatrixService;

    /**
     * Sent experiment results to ERS service.
     *
     * @param experimentResultsEntity - experiment entity
     * @param abstractExperiment      - experiment history
     */
    public void sentExperimentResults(ExperimentResultsEntity experimentResultsEntity,
                                      AbstractExperiment<?> abstractExperiment) {
        var experimentResultsRequest = new ExperimentResultsRequest();
        experimentResultsRequest.setExperimentResults(experimentResultsEntity);
        EvaluationResults evaluationResults =
                abstractExperiment.getHistory().get(experimentResultsEntity.getResultsIndex());
        var ersEvaluationRequestData = ErsEvaluationRequestData.builder()
                .ersRequest(experimentResultsRequest)
                .evaluationEntity(experimentResultsEntity.getExperiment())
                .evaluationResults(evaluationResults)
                .build();
        ersRequestService.saveEvaluationResults(ersEvaluationRequestData);
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
            var evaluationResultsResponse = ersRequestService.getEvaluationResults(requestId);
            var evaluationResultsDto = evaluationResultsMapper.map(evaluationResultsResponse);
            var confusionMatrixData = confusionMatrixService.proceedConfusionMatrix(evaluationResultsResponse);
            evaluationResultsDto.setConfusionMatrix(evaluationResultsMapper.map(confusionMatrixData));
            evaluationResultsStatus = EvaluationResultsStatus.RESULTS_RECEIVED;
            evaluationResultsDto.setEvaluationResultsStatus(new EnumDto(evaluationResultsStatus.name(),
                    evaluationResultsStatus.getDescription()));
            return evaluationResultsDto;
        } catch (InternalServiceUnavailableException ex) {
            evaluationResultsStatus = EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE;
        } catch (ErsBadRequestException ex) {
            evaluationResultsStatus = handleBadRequest(ex);
        } catch (Exception ex) {
            evaluationResultsStatus = EvaluationResultsStatus.ERROR;
        }
        return buildEvaluationResultsDto(evaluationResultsStatus);
    }

    private EvaluationResultsStatus handleBadRequest(ErsBadRequestException badRequestEx) {
        if (ErsErrorCode.RESULTS_NOT_FOUND.equals(badRequestEx.getErsErrorCode())) {
            return EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND;
        }
        return EvaluationResultsStatus.ERROR;
    }
}
