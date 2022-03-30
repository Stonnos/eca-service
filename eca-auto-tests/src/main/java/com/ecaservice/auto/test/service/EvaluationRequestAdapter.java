package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.dto.BaseEvaluationRequestDto;
import com.ecaservice.auto.test.dto.BaseTestStepDto;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.BaseTestStepEntity;
import com.ecaservice.auto.test.mapping.BaseEvaluationRequestMapper;
import com.ecaservice.auto.test.mapping.BaseTestStepMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Evaluation request adapter.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationRequestAdapter {

    private final List<BaseEvaluationRequestMapper> evaluationRequestMappers;
    private final List<BaseTestStepMapper> testStepMappers;

    /**
     * Converts evaluation request entity to its dto model
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @return evaluation request dto
     */
    @SuppressWarnings("unchecked")
    public BaseEvaluationRequestDto proceed(BaseEvaluationRequestEntity evaluationRequestEntity) {
        return (BaseEvaluationRequestDto) getMapper(evaluationRequestEntity).map(evaluationRequestEntity);
    }

    /**
     * Converts test steps entities to dto models.
     *
     * @param testSteps - test steps entities
     * @return test steps dto list
     */
    @SuppressWarnings("unchecked")
    public List<BaseTestStepDto> proceed(List<BaseTestStepEntity> testSteps) {
        return testSteps.stream()
                .map(testStep -> (BaseTestStepDto) getMapper(testStep).map(testStep))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private BaseEvaluationRequestMapper getMapper(BaseEvaluationRequestEntity evaluationRequestEntity) {
        return evaluationRequestMappers.stream()
                .filter(mapper -> mapper.canMap(evaluationRequestEntity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Can't map [%s] evaluation request",
                        evaluationRequestEntity.getClass().getSimpleName())));
    }

    @SuppressWarnings("unchecked")
    private BaseTestStepMapper getMapper(BaseTestStepEntity baseTestStepEntity) {
        return testStepMappers.stream()
                .filter(mapper -> mapper.canMap(baseTestStepEntity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Can't map [%s] test step",
                        baseTestStepEntity.getClass().getSimpleName())));
    }
}
