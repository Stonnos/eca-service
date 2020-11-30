package com.ecaservice.external.api.mapping;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestDto;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link EcaRequestMapper} class functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(EcaRequestMapperImpl.class)
class EcaRequestMapperTest {

    @Inject
    private EcaRequestMapper ecaRequestMapper;

    private EvaluationRequestDto evaluationRequestDto;

    @BeforeEach
    void init() {
        evaluationRequestDto = createEvaluationRequestDto();
    }

    @Test
    void testMapEvaluationRequestDto() {
        EvaluationRequestEntity evaluationRequestEntity = ecaRequestMapper.map(evaluationRequestDto);
        assertThat(evaluationRequestEntity).isNotNull();
        assertThat(evaluationRequestEntity.getEvaluationMethod()).isEqualTo(evaluationRequestDto.getEvaluationMethod());
        assertThat(evaluationRequestEntity.getNumFolds()).isEqualTo(evaluationRequestDto.getNumFolds());
        assertThat(evaluationRequestEntity.getNumTests()).isEqualTo(evaluationRequestDto.getNumTests());
        assertThat(evaluationRequestEntity.getSeed()).isEqualTo(evaluationRequestDto.getSeed());
        assertThat(evaluationRequestEntity.getClassifierOptionsJson()).isNotEmpty();
    }
}
