package com.ecaservice.external.api.test.report;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import com.ecaservice.test.common.report.AbstractCsvTestResultsReportGenerator;
import com.ecaservice.test.common.report.TestResultsCounter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequiredArgsConstructor
public class ExternalApiTestResultsCsvReportGenerator extends AbstractCsvTestResultsReportGenerator<JobEntity> {

    private static final String[] TEST_RESULTS_HEADERS = {
            "Display name",
            "started",
            "finished",
            "total time",
            "result",
            "total matched",
            "total not matched",
            "total not found",
            "expected request status",
            "actual request status",
            "request status match result",
            "expected model url",
            "actual model url",
            "model url match result",
            "expected pct correct",
            "actual pct correct",
            "pct correct match result",
            "expected pct incorrect",
            "actual pct incorrect",
            "pct incorrect match result",
            "expected mean abs. error",
            "actual mean abs. error",
            "mean abs. error match result",
            "request",
            "response",
            "details"
    };

    private static final String[] HEADERS_TOTALS = {
            "test uuid",
            "status",
            "threads",
            "total time",
            "success",
            "failed",
            "errors"
    };

    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final AutoTestRepository autoTestRepository;

    @Override
    protected String[] getResultsReportHeaders() {
        return TEST_RESULTS_HEADERS;
    }

    @Override
    protected String[] getTotalReportHeaders() {
        return HEADERS_TOTALS;
    }

    @Override
    protected void printReportTestResults(CSVPrinter printer, JobEntity jobEntity,
                                          TestResultsCounter testResultsCounter)
            throws IOException {
        Pageable pageRequest = PageRequest.of(0, externalApiTestsConfig.getPageSize());
        Page<AutoTestEntity> page;
        do {
            page = autoTestRepository.findAllByJob(jobEntity, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one entity has been fetched");
                break;
            } else {
                for (AutoTestEntity autoTestEntity : page.getContent()) {
                    autoTestEntity.getTestResult().apply(testResultsCounter);
                    printer.printRecord(Arrays.asList(
                            autoTestEntity.getDisplayName(),
                            autoTestEntity.getStarted(),
                            autoTestEntity.getFinished(),
                            totalTime(autoTestEntity.getStarted(), autoTestEntity.getFinished()),
                            autoTestEntity.getTestResult(),
                            autoTestEntity.getTotalMatched(),
                            autoTestEntity.getTotalNotMatched(),
                            autoTestEntity.getTotalNotFound(),
                            autoTestEntity.getExpectedRequestStatus(),
                            autoTestEntity.getActualRequestStatus(),
                            autoTestEntity.getRequestStatusMatchResult(),
                            autoTestEntity.getExpectedModelUrl(),
                            autoTestEntity.getActualModelUrl(),
                            autoTestEntity.getModelUrlMatchResult(),
                            autoTestEntity.getExpectedPctCorrect(),
                            autoTestEntity.getActualPctCorrect(),
                            autoTestEntity.getPctCorrectMatchResult(),
                            autoTestEntity.getExpectedPctIncorrect(),
                            autoTestEntity.getActualPctIncorrect(),
                            autoTestEntity.getPctIncorrectMatchResult(),
                            autoTestEntity.getExpectedMeanAbsoluteError(),
                            autoTestEntity.getActualMeanAbsoluteError(),
                            autoTestEntity.getMeanAbsoluteErrorMatchResult(),
                            autoTestEntity.getRequest(),
                            autoTestEntity.getResponse(),
                            autoTestEntity.getDetails()
                    ));
                }
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
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
