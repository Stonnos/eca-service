package com.ecaservice.auto.test.service.step;

import com.ecaservice.auto.test.event.model.AbstractTestStepEvent;

/**
 * Test step handler interface.
 *
 * @param <E> - test step event generic type
 * @author Roman Batygin
 */
public interface AbstractTestStepHandler<E extends AbstractTestStepEvent> {

    /**
     * Handles test step event.
     *
     * @param testStep - test step event
     */
    void handle(E testStep);
}
