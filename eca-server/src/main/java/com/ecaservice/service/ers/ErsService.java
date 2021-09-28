package com.ecaservice.service.ers;

import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.mapping.GetEvaluationResultsMapper;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.ExperimentResultsRequest;
import com.ecaservice.model.experiment.ExperimentResultsRequestSource;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.common.web.util.ValidationErrorHelper.hasError;
import static com.ecaservice.util.ResponseHelper.retrieveValidationErrors;
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
     * @param abstractExperiment      - experiment history
     * @param source                  - experiment results request source
     */
    public void sentExperimentResults(ExperimentResultsEntity experimentResultsEntity,
                                      AbstractExperiment<?> abstractExperiment, ExperimentResultsRequestSource source) {
        ExperimentResultsRequest experimentResultsRequest = new ExperimentResultsRequest();
        experimentResultsRequest.setRequestSource(source);
        experimentResultsRequest.setExperimentResults(experimentResultsEntity);
        EvaluationResults evaluationResults =
                abstractExperiment.getHistory().get(experimentResultsEntity.getResultsIndex());
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
            var evaluationResultsResponse = ersRequestService.getEvaluationResults(requestId);
            var evaluationResultsDto = evaluationResultsMapper.map(evaluationResultsResponse);
            evaluationResultsStatus = EvaluationResultsStatus.RESULTS_RECEIVED;
            evaluationResultsDto.setEvaluationResultsStatus(new EnumDto(evaluationResultsStatus.name(),
                    evaluationResultsStatus.getDescription()));
        } catch (FeignException.ServiceUnavailable ex) {
            log.error("Service unavailable error while fetching evaluation results for request id [{}]: {}", requestId,
                    ex.getMessage());
            evaluationResultsStatus = EvaluationResultsStatus.ERS_SERVICE_UNAVAILABLE;
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while fetching evaluation results for request id [{}]: {}", requestId,
                    ex.getMessage());
            evaluationResultsStatus = handleBadRequest(ex);
        } catch (Exception ex) {
            log.error("Unknown error while fetching evaluation results for request id [{}]: {}", requestId,
                    ex.getMessage());
            evaluationResultsStatus = EvaluationResultsStatus.ERROR;
        }
        return buildEvaluationResultsDto(evaluationResultsStatus);
    }

    private EvaluationResultsStatus handleBadRequest(FeignException.BadRequest badRequestEx) {
        try {
            var validationErrors = retrieveValidationErrors(badRequestEx.contentUTF8());
            if (hasError(ErsErrorCode.RESULTS_NOT_FOUND.name(), validationErrors)) {
                return EvaluationResultsStatus.EVALUATION_RESULTS_NOT_FOUND;
            }
        } catch (Exception ex) {
            log.error("Error while parsing bad request response: {}", ex.getMessage());
        }
        return EvaluationResultsStatus.ERROR;
    }
}
