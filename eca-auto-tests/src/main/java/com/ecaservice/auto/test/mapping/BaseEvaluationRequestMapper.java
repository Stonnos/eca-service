package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.BaseEvaluationRequestDto;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;

/**
 * Evaluation request dto mapper.
 *
 * @param <S> - source evaluation request entity
 * @param <T> - target evaluation request dto
 * @author Roman Batygin
 */
public abstract class BaseEvaluationRequestMapper<S extends BaseEvaluationRequestEntity,
        T extends BaseEvaluationRequestDto> extends BaseTestMapper<S, T> {

    /**
     * Constructor with parameters.
     *
     * @param type - evaluation request type
     */
    protected BaseEvaluationRequestMapper(Class<S> type) {
        super(type);
    }
}
