package com.ecaservice.load.test.report.service;

import com.ecaservice.load.test.entity.TestResultVisitor;
import lombok.Getter;

/**
 * Test results counter.
 *
 * @author Roman Batygin
 */
@Getter
public class TestResultsCounter implements TestResultVisitor {

    private int passed;
    private int failed;
    private int errors;

    @Override
    public void casePassed() {
        passed++;
    }

    @Override
    public void caseFailed() {
        failed++;
    }

    @Override
    public void caseError() {
        errors++;
    }
}
