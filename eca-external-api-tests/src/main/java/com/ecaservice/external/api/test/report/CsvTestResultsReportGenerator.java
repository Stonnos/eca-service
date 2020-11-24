package com.ecaservice.external.api.test.report;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.entity.AutoTestEntity;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.AutoTestRepository;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ecaservice.external.api.test.util.Utils.totalTime;

/**
 * Csv report generator
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CsvTestResultsReportGenerator implements TestResultsReportGenerator {

    private static final char HEADER_DELIMITER = ';';
    private static final String TEST_RUN_LOGS_CSV = "test-run-logs.csv";
    private static final String TEST_RUN_TOTALS_CSV = "test-run-totals.csv";

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
    public void generateReport(JobEntity jobEntity, OutputStream outputStream) throws IOException {
        TestResultsCounter counter = new TestResultsCounter();
        @Cleanup ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        @Cleanup OutputStreamWriter writer = new OutputStreamWriter(zipOutputStream, StandardCharsets.UTF_8);

        zipOutputStream.putNextEntry(new ZipEntry(TEST_RUN_LOGS_CSV));
        printReportTestResults(writer, jobEntity, counter);
        writer.flush();
        zipOutputStream.flush();
        zipOutputStream.closeEntry();

        zipOutputStream.putNextEntry(new ZipEntry(TEST_RUN_TOTALS_CSV));
        printReportTotal(writer, jobEntity, counter);
        writer.flush();
        zipOutputStream.flush();
        zipOutputStream.closeEntry();
    }

    private void printReportTestResults(OutputStreamWriter writer,
                                        JobEntity jobEntity,
                                        TestResultsCounter testResultsCounter) throws IOException {
        CSVPrinter printer = new CSVPrinter(writer,
                CSVFormat.EXCEL.withHeader(TEST_RESULTS_HEADERS).withDelimiter(HEADER_DELIMITER));
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

    private void printReportTotal(OutputStreamWriter writer,
                                  JobEntity jobEntity,
                                  TestResultsCounter testResultsCounter) throws IOException {
        CSVPrinter printer = new CSVPrinter(writer,
                CSVFormat.EXCEL.withHeader(HEADERS_TOTALS).withDelimiter(HEADER_DELIMITER));
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
