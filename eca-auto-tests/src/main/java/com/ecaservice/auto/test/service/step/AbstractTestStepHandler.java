package com.ecaservice.auto.test.service.step;

import com.ecaservice.auto.test.event.model.AbstractTestStepEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * Abstract test step handler.
 *
 * @param <E> - test step event generic type
 * @author Roman Batygin
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractTestStepHandler<E extends AbstractTestStepEvent> {

    private final Class<E> testStepEventClazz;

    /**
     * Checks that test step event can be handle.
     *
     * @param testStep - test step event
     * @return {@code true} if test step event can be handle, otherwise {@code false}
     */
    public boolean canHandle(E testStep) {
        return testStep != null && testStepEventClazz.isAssignableFrom(testStep.getClass());
    }

    /**
     * Handles test step event.
     *
     * @param testStep - test step event
     */
    public abstract void handle(E testStep);
}
