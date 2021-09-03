package com.ecaservice.external.api.mapping;

import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.entity.RequestStageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EvaluationStatusMapper} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(EvaluationStatusMapperImpl.class)
class EvaluationStatusMapperTest {

    private final Map<RequestStageType, EvaluationStatus> statusesMap = Map.of(
            RequestStageType.REQUEST_SENT, EvaluationStatus.IN_PROGRESS,
            RequestStageType.RESPONSE_RECEIVED, EvaluationStatus.IN_PROGRESS,
            RequestStageType.COMPLETED, EvaluationStatus.FINISHED,
            RequestStageType.ERROR, EvaluationStatus.ERROR,
            RequestStageType.READY, EvaluationStatus.IN_PROGRESS,
            RequestStageType.EXCEEDED, EvaluationStatus.TIMEOUT
    );

    @Inject
    private EvaluationStatusMapper evaluationStatusMapper;

    @Test
    void testMapRequestStages() {
        statusesMap.forEach((requestStageType, expectedEvaluationStatus) -> {
            var actualEvaluationStatus = evaluationStatusMapper.map(requestStageType);
            assertThat(actualEvaluationStatus).isEqualTo(expectedEvaluationStatus);
        });
    }
}
