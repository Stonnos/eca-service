package com.ecaservice.server.bpm.extract;

import org.camunda.bpm.engine.delegate.DelegateExecution;

/**
 * Value extractor from bpmn delegate execution.
 *
 * @param <T> - value generic type
 * @author Roman Batygin
 */
@FunctionalInterface
public interface ValueExtractor<T> {

    /**
     * Extracts value.
     *
     * @param execution - delegate execution
     * @return value
     */
    T extract(DelegateExecution execution);
}
