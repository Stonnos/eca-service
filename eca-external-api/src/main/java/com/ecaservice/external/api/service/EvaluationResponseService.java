package com.ecaservice.external.api.service;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.mapping.EvaluationStatusMapper;
import eca.core.evaluation.Evaluation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Class for response building.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResponseService {

    private static final String MODEL_DOWNLOAD_URL_FORMAT = "%s/download-model/%s";

    private final ExternalApiConfig externalApiConfig;
    private final EvaluationStatusMapper evaluationStatusMapper;
    private final EcaRequestService ecaRequestService;

    /**
     * Processes evaluation response.
     *
     * @param correlationId - request correlation id
     * @return evaluation response dto
     */
    public EvaluationResponseDto processResponse(String correlationId) {
        var ecaRequestEntity = ecaRequestService.getByCorrelationId(correlationId);
        var evaluationResponseDtoBuilder = EvaluationResponseDto.builder()
                .requestId(ecaRequestEntity.getCorrelationId());
        var evaluationStatus = evaluationStatusMapper.map(ecaRequestEntity.getRequestStage());
        if (EvaluationStatus.FINISHED.equals(evaluationStatus)) {
            Evaluation evaluation = null;
            evaluationResponseDtoBuilder.numTestInstances(BigInteger.valueOf((long) evaluation.numInstances()))
                    .numCorrect(BigInteger.valueOf((long) evaluation.correct()))
                    .numIncorrect(BigInteger.valueOf((long) evaluation.incorrect()))
                    .pctCorrect(BigDecimal.valueOf(evaluation.pctCorrect()))
                    .pctIncorrect(BigDecimal.valueOf(evaluation.pctIncorrect()))
                    .meanAbsoluteError(BigDecimal.valueOf(evaluation.meanAbsoluteError()))
                    .modelUrl(String.format(MODEL_DOWNLOAD_URL_FORMAT, externalApiConfig.getDownloadBaseUrl(),
                            ecaRequestEntity.getCorrelationId()));
        }
        return evaluationResponseDtoBuilder.build();
    }
}
