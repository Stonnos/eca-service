package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.dto.BaseEvaluationRequestDto;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.mapping.BaseEvaluationRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * Converts evaluation request entity to its dto model
     *
     * @param evaluationRequestEntity - evaluation request entity
     * @return evaluation request dto
     */
    @SuppressWarnings("unchecked")
    public BaseEvaluationRequestDto proceed(BaseEvaluationRequestEntity evaluationRequestEntity) {
        return getMapper(evaluationRequestEntity).map(evaluationRequestEntity);
    }

    @SuppressWarnings("unchecked")
    private BaseEvaluationRequestMapper getMapper(BaseEvaluationRequestEntity evaluationRequestEntity) {
        return evaluationRequestMappers.stream()
                .filter(mapper -> mapper.canMap(evaluationRequestEntity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Can't map [%s] evaluation request",
                        evaluationRequestEntity.getClass().getSimpleName())));
    }
}
