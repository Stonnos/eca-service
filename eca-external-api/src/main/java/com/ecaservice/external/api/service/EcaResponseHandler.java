package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Eca response handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EcaResponseHandler {

    private final EcaRequestRepository ecaRequestRepository;

    /**
     * Handles response from eca - server.
     *
     * @param ecaRequestEntity   - eca request entity
     * @param evaluationResponse - evaluation response
     * @return evaluation response dto
     */
    public EvaluationResponseDto handleResponse(EcaRequestEntity ecaRequestEntity,
                                                EvaluationResponse evaluationResponse) {
        EvaluationResponseDto evaluationResponseDto = EvaluationResponseDto.builder()
                .requestId(evaluationResponse.getRequestId())
                .build();
        try {
            if (TechnicalStatus.SUCCESS.equals(evaluationResponse.getStatus())) {
                if (Optional.ofNullable(evaluationResponse.getEvaluationResults()).map(
                        EvaluationResults::getEvaluation).isPresent()) {
                    Evaluation evaluation = evaluationResponse.getEvaluationResults().getEvaluation();
                    evaluationResponseDto.setNumTestInstances(BigInteger.valueOf((long) evaluation.numInstances()));
                    evaluationResponseDto.setNumCorrect(BigInteger.valueOf((long) evaluation.correct()));
                    evaluationResponseDto.setNumIncorrect(BigInteger.valueOf((long) evaluation.incorrect()));
                    evaluationResponseDto.setPctCorrect(BigDecimal.valueOf(evaluation.pctCorrect()));
                    evaluationResponseDto.setPctIncorrect(BigDecimal.valueOf(evaluation.pctIncorrect()));
                    evaluationResponseDto.setMeanAbsoluteError(BigDecimal.valueOf(evaluation.meanAbsoluteError()));
                }
                evaluationResponseDto.setStatus(RequestStatus.SUCCESS);
            } else {
                evaluationResponseDto.setStatus(RequestStatus.ERROR);
            }
            ecaRequestEntity.setRequestStage(RequestStageType.COMPLETED);
        } catch (Exception ex) {
            log.error("There was an error while response [{}] handling: {}", ecaRequestEntity.getCorrelationId(),
                    ex.getMessage(), ex);
            ecaRequestEntity.setRequestStage(RequestStageType.ERROR);
            ecaRequestEntity.setErrorMessage(ex.getMessage());
        } finally {
            ecaRequestEntity.setEndDate(LocalDateTime.now());
            ecaRequestRepository.save(ecaRequestEntity);
        }
        return evaluationResponseDto;
    }
}
