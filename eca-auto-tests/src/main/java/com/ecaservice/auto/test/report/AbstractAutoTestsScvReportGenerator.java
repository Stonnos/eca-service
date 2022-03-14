package com.ecaservice.auto.test.report;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.BaseEvaluationRequestEntity;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.test.common.report.AbstractCsvTestResultsReportGenerator;
import com.ecaservice.test.common.report.TestResultsCounter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Abstract auto tests report generator service.
 *
 * @param <E> - request entity type
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAutoTestsScvReportGenerator<E extends BaseEvaluationRequestEntity>
        extends AbstractCsvTestResultsReportGenerator<AutoTestsJobEntity> {

    private static final String[] HEADERS_TOTALS = {
            "job uuid",
            "status",
            "total time",
            "success",
            "failed",
            "errors"
    };

    @Getter
    private final AutoTestType autoTestType;
    private final AutoTestsProperties autoTestsProperties;
    private final BaseEvaluationRequestRepository baseEvaluationRequestRepository;

    /**
     * Check that auto test report can be handled for specified job.
     *
     * @param autoTestsJobEntity - auto tests job entity
     * @return {@code true} if auto test report can be handled, otherwise {@link false}
     */
    public boolean canHandle(AutoTestsJobEntity autoTestsJobEntity) {
        return autoTestType.equals(autoTestsJobEntity.getAutoTestType());
    }

    protected abstract Page<E> getNextPage(AutoTestsJobEntity autoTestsJobEntity, Pageable pageable);

    protected abstract void printCsvRecord(CSVPrinter printer, E requestEntity) throws IOException;

    protected abstract void printAdditionalRecord(ZipOutputStream zipOutputStream,
                                                  OutputStreamWriter outputStreamWriter,
                                                  E requestEntity) throws IOException;

    @Override
    protected String[] getTotalReportHeaders() {
        return HEADERS_TOTALS;
    }

    @Override
    protected void printReportTotal(CSVPrinter printer, AutoTestsJobEntity jobEntity,
                                    TestResultsCounter testResultsCounter) throws IOException {
        LocalDateTime started = baseEvaluationRequestRepository.getMinStartedDate(jobEntity)
                .orElse(jobEntity.getStarted());
        LocalDateTime finished = baseEvaluationRequestRepository.getMaxFinishedDate(jobEntity)
                .orElse(jobEntity.getFinished());
        printer.printRecord(Arrays.asList(
                jobEntity.getJobUuid(),
                jobEntity.getExecutionStatus(),
                totalTime(started, finished),
                testResultsCounter.getPassed(),
                testResultsCounter.getFailed(),
                testResultsCounter.getErrors())
        );
    }

    @Override
    protected void printReportTestResults(CSVPrinter printer,
                                          AutoTestsJobEntity jobEntity,
                                          TestResultsCounter testResultsCounter) throws IOException {
        Pageable pageRequest = PageRequest.of(0, autoTestsProperties.getPageSize());
        Page<E> page;
        do {
            page = getNextPage(jobEntity, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one entity has been fetched");
                break;
            } else {
                for (var requestEntity : page.getContent()) {
                    requestEntity.getTestResult().apply(testResultsCounter);
                    printCsvRecord(printer, requestEntity);
                }
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }

    @Override
    protected void printAdditionalReportData(ZipOutputStream zipOutputStream,
                                             OutputStreamWriter outputStreamWriter,
                                             AutoTestsJobEntity autoTestsJobEntity) throws IOException {
        Pageable pageRequest = PageRequest.of(0, autoTestsProperties.getPageSize());
        Page<E> page;
        do {
            page = getNextPage(autoTestsJobEntity, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one entity has been fetched");
                break;
            } else {
                for (var requestEntity : page.getContent()) {
                    printAdditionalRecord(zipOutputStream, outputStreamWriter, requestEntity);
                }
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }
}
