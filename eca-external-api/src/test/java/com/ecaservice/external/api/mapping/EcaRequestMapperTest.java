package com.ecaservice.external.api.mapping;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.base.model.ExperimentType;
import com.ecaservice.external.api.dto.EvaluationErrorCode;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.ExperimentRequestEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import java.util.Map;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestDto;
import static com.ecaservice.external.api.TestHelperUtils.createExperimentRequestDto;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link EcaRequestMapper} class functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(EcaRequestMapperImpl.class)
class EcaRequestMapperTest {

    private final Map<ErrorCode, EvaluationErrorCode> errorCodesMap = Map.of(
            ErrorCode.INTERNAL_SERVER_ERROR, EvaluationErrorCode.INTERNAL_SERVER_ERROR,
            ErrorCode.SERVICE_UNAVAILABLE, EvaluationErrorCode.SERVICE_UNAVAILABLE,
            ErrorCode.CLASSIFIER_OPTIONS_NOT_FOUND, EvaluationErrorCode.CLASSIFIER_OPTIONS_NOT_FOUND,
            ErrorCode.TRAINING_DATA_NOT_FOUND, EvaluationErrorCode.TRAINING_DATA_NOT_FOUND,
            ErrorCode.INVALID_FIELD_VALUE, EvaluationErrorCode.INTERNAL_SERVER_ERROR
    );

    @Inject
    private EcaRequestMapper ecaRequestMapper;

    @Test
    void testMapEvaluationRequestDto() {
        var evaluationRequestDto = createEvaluationRequestDto();
        EvaluationRequestEntity evaluationRequestEntity = ecaRequestMapper.map(evaluationRequestDto);
        assertThat(evaluationRequestEntity).isNotNull();
        assertThat(evaluationRequestEntity.getEvaluationMethod()).isEqualTo(evaluationRequestDto.getEvaluationMethod());
        assertThat(evaluationRequestEntity.getNumFolds()).isEqualTo(evaluationRequestDto.getNumFolds());
        assertThat(evaluationRequestEntity.getNumTests()).isEqualTo(evaluationRequestDto.getNumTests());
        assertThat(evaluationRequestEntity.getSeed()).isEqualTo(evaluationRequestDto.getSeed());
        assertThat(evaluationRequestEntity.getClassifierOptionsJson()).isNotEmpty();
    }

    @Test
    void testMapExperimentRequestDto() {
        var experimentRequestDto = createExperimentRequestDto();
        ExperimentRequestEntity experimentRequestEntity = ecaRequestMapper.map(experimentRequestDto);
        assertThat(experimentRequestEntity).isNotNull();
        assertThat(experimentRequestEntity.getEvaluationMethod()).isEqualTo(experimentRequestDto.getEvaluationMethod());
        assertThat(experimentRequestEntity.getExperimentType()).isEqualTo(ExperimentType.RANDOM_FORESTS);
    }

    @Test
    void testMapErrorCodes() {
        errorCodesMap.forEach(((errorCode, expectedEvaluationErrorCode) -> {
            var actualEvaluationErrorCode = ecaRequestMapper.map(errorCode);
            assertThat(actualEvaluationErrorCode).isEqualTo(expectedEvaluationErrorCode);
        }));
    }
}
