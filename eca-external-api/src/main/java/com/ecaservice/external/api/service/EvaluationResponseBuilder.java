package com.ecaservice.external.api.service;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import com.ecaservice.external.api.util.Utils;
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
public class EvaluationResponseBuilder {

    private static final String MODEL_DOWNLOAD_URL_FORMAT = "%s/download-model/%s";

    private final ExternalApiConfig externalApiConfig;

    /**
     * Builds evaluation response dto.
     *
     * @param evaluationResponse - evaluation response from eca - server
     * @return evaluation response dto
     */
    public ResponseDto<EvaluationResponseDto> buildResponse(EvaluationResponse evaluationResponse,
                                                           EcaRequestEntity ecaRequestEntity) {
        RequestStatus requestStatus = RequestStatus.SUCCESS;
        EvaluationResponseDto.EvaluationResponseDtoBuilder builder =
                EvaluationResponseDto.builder().requestId(ecaRequestEntity.getCorrelationId());
        if (!RequestStageType.ERROR.equals(ecaRequestEntity.getRequestStage())) {
            Evaluation evaluation = evaluationResponse.getEvaluationResults().getEvaluation();
            builder.numTestInstances(BigInteger.valueOf((long) evaluation.numInstances()))
                    .numCorrect(BigInteger.valueOf((long) evaluation.correct()))
                    .numIncorrect(BigInteger.valueOf((long) evaluation.incorrect()))
                    .pctCorrect(BigDecimal.valueOf(evaluation.pctCorrect()))
                    .pctIncorrect(BigDecimal.valueOf(evaluation.pctIncorrect()))
                    .meanAbsoluteError(BigDecimal.valueOf(evaluation.meanAbsoluteError()))
                    .modelUrl(String.format(MODEL_DOWNLOAD_URL_FORMAT, externalApiConfig.getDownloadBaseUrl(),
                            ecaRequestEntity.getCorrelationId()));
        } else {
            requestStatus = RequestStatus.ERROR;
        }
        return Utils.buildResponse(requestStatus, builder.build());
    }
}
