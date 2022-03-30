package com.ecaservice.auto.test.mapping;

import com.ecaservice.auto.test.dto.BaseTestStepDto;
import com.ecaservice.auto.test.entity.autotest.BaseTestStepEntity;

/**
 * Test step mapper.
 *
 * @param <S> - source test step entity
 * @param <T> - target test step dto
 * @author Roman Batygin
 */
public abstract class BaseTestStepMapper<S extends BaseTestStepEntity, T extends BaseTestStepDto>
        extends BaseTestMapper<S, T> {

    /**
     * Constructor with parameters.
     *
     * @param type - evaluation request type
     */
    protected BaseTestStepMapper(Class<S> type) {
        super(type);
    }
}
