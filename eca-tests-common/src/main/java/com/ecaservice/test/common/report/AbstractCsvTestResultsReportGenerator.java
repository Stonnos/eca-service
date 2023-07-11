package com.ecaservice.test.common.report;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ecaservice.test.common.util.ZipUtils.flush;

/**
 * Csv report generator.
 *
 * @param <T> - report data type
 * @author Roman Batygin
 */
@Slf4j
public abstract class AbstractCsvTestResultsReportGenerator<T> implements TestResultsReportGenerator<T> {

    private static final char HEADER_DELIMITER = ';';
    private static final String TEST_RUN_LOGS_CSV = "test-run-logs.csv";
    private static final String TEST_RUN_TOTALS_CSV = "test-run-totals.csv";

    @Override
    public void generateReport(T data, OutputStream outputStream) throws IOException {
        var counter = new TestResultsCounter();
        var resultsCsvFormat = CSVFormat.EXCEL.builder()
                .setHeader(getResultsReportHeaders())
                .setDelimiter(getHeaderDelimiter())
                .build();
        var totalCsvFormat = CSVFormat.EXCEL.builder()
                .setHeader(getTotalReportHeaders())
                .setDelimiter(getHeaderDelimiter())
                .build();
        @Cleanup var zipOutputStream = new ZipOutputStream(outputStream);
        @Cleanup var writer = new OutputStreamWriter(zipOutputStream, StandardCharsets.UTF_8);

        log.info("Starting to write file [{}] into zip archive", TEST_RUN_LOGS_CSV);
        zipOutputStream.putNextEntry(new ZipEntry(TEST_RUN_LOGS_CSV));
        @Cleanup var resultsPrinter = new CSVPrinter(writer, resultsCsvFormat);
        printReportTestResults(resultsPrinter, data, counter);
        flush(zipOutputStream, writer);
        log.info("File [{}] has been written into zip archive", TEST_RUN_LOGS_CSV);

        log.info("Starting to write file [{}] into zip archive", TEST_RUN_TOTALS_CSV);
        zipOutputStream.putNextEntry(new ZipEntry(TEST_RUN_TOTALS_CSV));
        @Cleanup var totalPrinter = new CSVPrinter(writer, totalCsvFormat);
        printReportTotal(totalPrinter, data, counter);
        flush(zipOutputStream, writer);
        log.info("File [{}] has been written into zip archive", TEST_RUN_TOTALS_CSV);

        printAdditionalReportData(zipOutputStream, writer, data);
    }

    protected void printAdditionalReportData(ZipOutputStream zipOutputStream,
                                             OutputStreamWriter outputStreamWriter,
                                             T data) throws IOException {
        //empty implementation
    }

    protected abstract String[] getResultsReportHeaders();

    protected abstract String[] getTotalReportHeaders();

    protected char getHeaderDelimiter() {
        return HEADER_DELIMITER;
    }

    protected abstract void printReportTestResults(CSVPrinter csvPrinter, T data,
                                                   TestResultsCounter testResultsCounter) throws IOException;

    protected abstract void printReportTotal(CSVPrinter csvPrinter, T data, TestResultsCounter testResultsCounter)
            throws IOException;
}
