package com.ecaservice.external.api.test.entity;

import eca.core.DescriptiveEnum;
import lombok.RequiredArgsConstructor;

/**
 * Test result enum.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum TestResult implements DescriptiveEnum {

    /**
     * Test passed
     */
    PASSED("Успешно") {
        @Override
        public void apply(TestResultVisitor visitor) {
            visitor.casePassed();
        }
    },

    /**
     * Test failed
     */
    FAILED("Неуспешно") {
        @Override
        public void apply(TestResultVisitor visitor) {
            visitor.caseFailed();
        }
    },

    /**
     * Unknown error
     */
    ERROR("Ошибка") {
        @Override
        public void apply(TestResultVisitor visitor) {
            visitor.caseError();
        }
    },

    /**
     * Unknown result
     */
    UNKNOWN("Неизвестно") {
        @Override
        public void apply(TestResultVisitor visitor) {
            visitor.caseUnknown();
        }
    };

    private final String description;

    /**
     * Apply visitor.
     *
     * @param visitor - visitor object
     */
    public abstract void apply(TestResultVisitor visitor);

    @Override
    public String getDescription() {
        return description;
    }
}
