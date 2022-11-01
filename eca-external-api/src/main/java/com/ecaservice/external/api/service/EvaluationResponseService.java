package com.ecaservice.external.api.service;

import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.dto.ExperimentResultsResponseDto;
import com.ecaservice.external.api.mapping.EvaluationStatusMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Class for response building.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResponseService {

    private final EvaluationStatusMapper evaluationStatusMapper;
    private final EcaRequestService ecaRequestService;

    /**
     * Processes evaluation results response.
     *
     * @param correlationId - request correlation id
     * @return evaluation response dto
     */
    public EvaluationResultsResponseDto processEvaluationResultsResponse(String correlationId) {
        log.info("Starting to process evaluation results response [{}]", correlationId);
        var ecaRequestEntity = ecaRequestService.getEvaluationRequest(correlationId);
        var responseBuilder = EvaluationResultsResponseDto.builder()
                .requestId(ecaRequestEntity.getCorrelationId());
        var evaluationStatus = evaluationStatusMapper.map(ecaRequestEntity.getRequestStage());
        responseBuilder.evaluationStatus(evaluationStatus);
        if (EvaluationStatus.FINISHED.equals(evaluationStatus)) {
            responseBuilder.numTestInstances(ecaRequestEntity.getNumTestInstances())
                    .numCorrect(ecaRequestEntity.getNumCorrect())
                    .numIncorrect(ecaRequestEntity.getNumIncorrect())
                    .pctCorrect(ecaRequestEntity.getPctCorrect())
                    .pctIncorrect(ecaRequestEntity.getPctIncorrect())
                    .meanAbsoluteError(ecaRequestEntity.getMeanAbsoluteError())
                    .modelUrl(ecaRequestEntity.getClassifierDownloadUrl());
        } else if (EvaluationStatus.ERROR.equals(evaluationStatus)) {
            responseBuilder.errorCode(ecaRequestEntity.getErrorCode());
        }
        log.info("Evaluation [{}] results response has been built", correlationId);
        return responseBuilder.build();
    }

    /**
     * Processes experiment results response.
     *
     * @param correlationId - request correlation id
     * @return experiment response dto
     */
    public ExperimentResultsResponseDto processExperimentResultsResponse(String correlationId) {
        log.info("Starting to process experiment results response [{}]", correlationId);
        var ecaRequestEntity = ecaRequestService.getExperimentRequest(correlationId);
        var responseBuilder = ExperimentResultsResponseDto.builder()
                .requestId(ecaRequestEntity.getCorrelationId());
        var evaluationStatus = evaluationStatusMapper.map(ecaRequestEntity.getRequestStage());
        responseBuilder.evaluationStatus(evaluationStatus);
        if (EvaluationStatus.FINISHED.equals(evaluationStatus)) {
            responseBuilder.experimentModelUrl(ecaRequestEntity.getExperimentDownloadUrl());
        } else if (EvaluationStatus.ERROR.equals(evaluationStatus)) {
            responseBuilder.errorCode(ecaRequestEntity.getErrorCode());
        }
        log.info("Evaluation [{}] response has been built", correlationId);
        return responseBuilder.build();
    }
}
