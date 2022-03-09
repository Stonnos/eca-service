package com.ecaservice.auto.test.report;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Auto tests report generator service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AutoTestsScvReportGenerator extends AbstractCsvTestResultsReportGenerator<AutoTestsJobEntity> {

    private static final String[] TEST_RESULTS_HEADERS = {
            "experiment type",
            "experiment request id",
            "evaluation method",
            "started",
            "finished",
            "total time",
            "test result",
            "execution status",
            "total matched",
            "total not matched",
            "total not found",
            "relation name",
            "num instances",
            "num attributes",
            "new request email received",
            "in progress request email received",
            "finished request email received",
            "error request email received",
            "timeout request email received",
            "expected request status",
            "actual request status",
            "request status match result",
            "details"
    };

    private static final String[] HEADERS_TOTALS = {
            "job uuid",
            "status",
            "total time",
            "success",
            "failed",
            "errors"
    };

    private final AutoTestsProperties autoTestsProperties;
    private final ExperimentRequestRepository experimentRequestRepository;

    @Override
    protected String[] getResultsReportHeaders() {
        return TEST_RESULTS_HEADERS;
    }

    @Override
    protected String[] getTotalReportHeaders() {
        return HEADERS_TOTALS;
    }

    @Override
    protected void printReportTestResults(CSVPrinter printer, AutoTestsJobEntity jobEntity,
                                          TestResultsCounter testResultsCounter) throws IOException {
        Pageable pageRequest = PageRequest.of(0, autoTestsProperties.getPageSize());
        Page<ExperimentRequestEntity> page;
        do {
            page = experimentRequestRepository.findAllByJob(jobEntity, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one entity has been fetched");
                break;
            } else {
                for (ExperimentRequestEntity experimentRequestEntity : page.getContent()) {
                    experimentRequestEntity.getTestResult().apply(testResultsCounter);
                    printer.printRecord(Arrays.asList(
                            experimentRequestEntity.getExperimentType().getDescription(),
                            experimentRequestEntity.getRequestId(),
                            experimentRequestEntity.getEvaluationMethod().getDescription(),
                            experimentRequestEntity.getStarted(),
                            experimentRequestEntity.getFinished(),
                            totalTime(experimentRequestEntity.getStarted(), experimentRequestEntity.getFinished()),
                            experimentRequestEntity.getTestResult(),
                            experimentRequestEntity.getExecutionStatus(),
                            experimentRequestEntity.getTotalMatched(),
                            experimentRequestEntity.getTotalNotMatched(),
                            experimentRequestEntity.getTotalNotFound(),
                            experimentRequestEntity.getRelationName(),
                            experimentRequestEntity.getNumInstances(),
                            experimentRequestEntity.getNumAttributes(),
                            experimentRequestEntity.isNewStatusEmailReceived(),
                            experimentRequestEntity.isInProgressStatusEmailReceived(),
                            experimentRequestEntity.isFinishedStatusEmailReceived(),
                            experimentRequestEntity.isErrorStatusEmailReceived(),
                            experimentRequestEntity.isTimeoutStatusEmailReceived(),
                            experimentRequestEntity.getExpectedResultsSize(),
                            experimentRequestEntity.getActualResultsSize(),
                            experimentRequestEntity.getResultsSizeMatchResult(),
                            experimentRequestEntity.getDetails()
                    ));
                }
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }

    @Override
    protected void printReportTotal(CSVPrinter printer, AutoTestsJobEntity jobEntity,
                                    TestResultsCounter testResultsCounter) throws IOException {
        LocalDateTime started = experimentRequestRepository.getMinStartedDate(jobEntity).orElse(jobEntity.getStarted());
        LocalDateTime finished =
                experimentRequestRepository.getMaxFinishedDate(jobEntity).orElse(jobEntity.getFinished());
        printer.printRecord(Arrays.asList(
                jobEntity.getJobUuid(),
                jobEntity.getExecutionStatus(),
                totalTime(started, finished),
                testResultsCounter.getPassed(),
                testResultsCounter.getFailed(),
                testResultsCounter.getErrors())
        );
    }
}
