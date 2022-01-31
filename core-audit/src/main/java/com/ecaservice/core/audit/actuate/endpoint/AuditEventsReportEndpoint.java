package com.ecaservice.core.audit.actuate.endpoint;

import com.ecaservice.core.audit.service.report.impl.AuditEventCsvReportGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Audit events report actuator endpoint.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@Endpoint(id = "auditevents")
@RequiredArgsConstructor
public class AuditEventsReportEndpoint {

    private final AuditEventCsvReportGenerator auditEventCsvReportGenerator;

    /**
     * Gets audit events report in csv format.
     *
     * @return audit events report
     * @throws IOException in case of I/O errors
     */
    @ReadOperation(produces = MediaType.TEXT_PLAIN_VALUE)
    public String getAuditEvents() throws IOException {
        return auditEventCsvReportGenerator.generate();
    }
}
