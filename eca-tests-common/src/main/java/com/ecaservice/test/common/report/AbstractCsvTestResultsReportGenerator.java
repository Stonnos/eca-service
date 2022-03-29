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
        @Cleanup var zipOutputStream = new ZipOutputStream(outputStream);
        @Cleanup var writer = new OutputStreamWriter(zipOutputStream, StandardCharsets.UTF_8);

        zipOutputStream.putNextEntry(new ZipEntry(TEST_RUN_LOGS_CSV));
        @Cleanup var resultsPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withHeader(getResultsReportHeaders())
                .withDelimiter(getHeaderDelimiter()));
        printReportTestResults(resultsPrinter, data, counter);
        flush(zipOutputStream, writer);

        zipOutputStream.putNextEntry(new ZipEntry(TEST_RUN_TOTALS_CSV));
        @Cleanup var totalPrinter = new CSVPrinter(writer, CSVFormat.EXCEL.withHeader(getTotalReportHeaders())
                .withDelimiter(getHeaderDelimiter()));
        printReportTotal(totalPrinter, data, counter);
        flush(zipOutputStream, writer);

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
