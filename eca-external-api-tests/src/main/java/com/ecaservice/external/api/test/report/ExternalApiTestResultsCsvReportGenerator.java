package com.ecaservice.external.api.test.report;

import com.ecaservice.external.api.test.dto.AutoTestType;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.test.common.report.AbstractCsvTestResultsReportGenerator;
import com.ecaservice.test.common.report.TestResultsCounter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * External api auto tests csv report generator.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ExternalApiTestResultsCsvReportGenerator
        extends AbstractCsvTestResultsReportGenerator<JobEntity> {

    private static final String[] HEADERS_TOTALS = {
            "test uuid",
            "status",
            "threads",
            "total time",
            "success",
            "failed",
            "errors"
    };

    @Getter
    private final AutoTestType autoTestType;

    @Override
    protected String[] getTotalReportHeaders() {
        return HEADERS_TOTALS;
    }

    @Override
    protected void printReportTotal(CSVPrinter printer, JobEntity jobEntity, TestResultsCounter testResultsCounter)
            throws IOException {
        printer.printRecord(Arrays.asList(
                jobEntity.getJobUuid(),
                jobEntity.getExecutionStatus(),
                jobEntity.getNumThreads(),
                totalTime(jobEntity.getStarted(), jobEntity.getFinished()),
                testResultsCounter.getPassed(),
                testResultsCounter.getFailed(),
                testResultsCounter.getErrors())
        );
    }
}
