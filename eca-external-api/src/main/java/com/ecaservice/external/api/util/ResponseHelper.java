package com.ecaservice.external.api.util;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.entity.EcaRequestEntity;
import com.ecaservice.external.api.entity.RequestStageType;
import eca.core.evaluation.Evaluation;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Response helper class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ResponseHelper {

    /**
     * Builds evaluation response dto.
     *
     * @param evaluationResponse - evaluation response from eca - server
     * @return evaluation response dto
     */
    public static EvaluationResponseDto buildResponse(EvaluationResponse evaluationResponse,
                                                      EcaRequestEntity ecaRequestEntity) {
        EvaluationResponseDto.EvaluationResponseDtoBuilder builder = EvaluationResponseDto.builder();
        if (!RequestStageType.ERROR.equals(ecaRequestEntity.getRequestStage())) {
            Evaluation evaluation = evaluationResponse.getEvaluationResults().getEvaluation();
            builder.numTestInstances(BigInteger.valueOf((long) evaluation.numInstances()))
                    .numCorrect(BigInteger.valueOf((long) evaluation.correct()))
                    .numIncorrect(BigInteger.valueOf((long) evaluation.incorrect()))
                    .pctCorrect(BigDecimal.valueOf(evaluation.pctCorrect()))
                    .pctIncorrect(BigDecimal.valueOf(evaluation.pctIncorrect()))
                    .meanAbsoluteError(BigDecimal.valueOf(evaluation.meanAbsoluteError()))
                    .status(RequestStatus.SUCCESS);
        } else {
            builder.status(RequestStatus.ERROR);
        }
        return builder.build();
    }
}
