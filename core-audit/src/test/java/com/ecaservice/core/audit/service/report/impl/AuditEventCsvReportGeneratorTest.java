package com.ecaservice.core.audit.service.report.impl;

import com.ecaservice.core.audit.AbstractJpaTest;
import com.ecaservice.core.audit.repository.AuditCodeRepository;
import com.ecaservice.core.audit.repository.AuditGroupRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static com.ecaservice.core.audit.TestHelperUtils.createAuditCodeEntity;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link AuditEventCsvReportGenerator} class.
 *
 * @author Roman Batygin
 */
@Import(AuditEventCsvReportGenerator.class)
class AuditEventCsvReportGeneratorTest extends AbstractJpaTest {

    private static final char HEADER_DELIMITER = ';';
    private static final List<String> REPORT_HEADERS = List.of("Audit code", "Code description", "Group code",
            "Group description");
    private static final int HEADER_IDX = 0;

    @Inject
    private AuditCodeRepository auditCodeRepository;
    @Inject
    private AuditGroupRepository auditGroupRepository;

    @Inject
    private AuditEventCsvReportGenerator auditEventCsvReportGenerator;

    @Override
    public void init() {
        var auditCode = createAuditCodeEntity();
        auditGroupRepository.save(auditCode.getAuditGroup());
        auditCodeRepository.save(auditCode);
    }

    @Test
    void testGenerateCsvReport() throws IOException {
        String reportString = auditEventCsvReportGenerator.generate();
        assertThat(reportString).isNotNull();
        var auditCodes = auditCodeRepository.findAll();
        var rows = reportString.split("\r\n");
        assertThat(rows).hasSize(auditCodes.size() + 1);
        assertThat(rows[HEADER_IDX]).isEqualTo(StringUtils.join(REPORT_HEADERS, HEADER_DELIMITER));
        IntStream.range(1, rows.length).forEach(i -> {
            var auditCode = auditCodes.get(i - 1);
            var expectedRow = StringUtils.join(
                    List.of(auditCode.getId(), auditCode.getTitle(), auditCode.getAuditGroup().getId(),
                            auditCode.getAuditGroup().getTitle()), HEADER_DELIMITER);
            assertThat(rows[i]).isEqualTo(expectedRow);
        });
    }
}
