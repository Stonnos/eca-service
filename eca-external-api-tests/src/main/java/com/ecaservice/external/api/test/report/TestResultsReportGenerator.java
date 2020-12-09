package com.ecaservice.external.api.test.report;

import com.ecaservice.external.api.test.entity.JobEntity;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Test results report generator.
 *
 * @author Roman Batygin
 */
public interface TestResultsReportGenerator {


    /**
     * Generates report for specified job.
     *
     * @param jobEntity    - job entity
     * @param outputStream - output stream
     * @throws IOException in case of I/O error
     */
    void generateReport(JobEntity jobEntity, OutputStream outputStream) throws IOException;
}
