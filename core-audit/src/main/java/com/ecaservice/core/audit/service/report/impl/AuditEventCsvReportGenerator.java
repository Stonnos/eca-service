package com.ecaservice.core.audit.service.report.impl;

import com.ecaservice.core.audit.entity.AuditCodeEntity;
import com.ecaservice.core.audit.repository.AuditCodeRepository;
import com.ecaservice.core.audit.service.report.AuditEventReportGenerator;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Audit events CSV report generator.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventCsvReportGenerator implements AuditEventReportGenerator<String> {

    private static final char HEADER_DELIMITER = ';';
    private static final String[] REPORT_HEADERS = {"Audit code", "Code description", "Group code",
            "Group description"};

    private final AuditCodeRepository auditCodeRepository;

    @Override
    public String generate() throws IOException {
        log.info("Gets audit events csv report");
        @Cleanup StringWriter stringWriter = new StringWriter();
        @Cleanup var csvPrinter = new CSVPrinter(stringWriter,
                CSVFormat.EXCEL.withHeader(REPORT_HEADERS).withDelimiter(HEADER_DELIMITER));
        var auditCodes = auditCodeRepository.findAll();
        log.info("Fetched [{}] audit events", auditCodes.size());
        printReport(auditCodes, csvPrinter);
        String resultReport = stringWriter.toString();
        log.info("Audit events csv report has been generated");
        return resultReport;
    }

    private void printReport(List<AuditCodeEntity> auditCodes, CSVPrinter csvPrinter) throws IOException {
        for (var auditCode : auditCodes) {
            csvPrinter.printRecord(Arrays.asList(
                    auditCode.getId(),
                    auditCode.getTitle(),
                    auditCode.getAuditGroup().getId(),
                    auditCode.getAuditGroup().getTitle()
            ));
        }
    }
}
