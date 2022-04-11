package com.ecaservice.core.audit.service.report;

import java.io.IOException;

/**
 * Audit event report generator.
 *
 * @param <T> - report generic type
 * @author Roman Batygin
 */
public interface AuditEventReportGenerator<T> {

    /**
     * Generates audit event report.
     *
     * @return audit event report
     * @throws IOException in case of I/O error
     */
    T generate() throws IOException;
}
