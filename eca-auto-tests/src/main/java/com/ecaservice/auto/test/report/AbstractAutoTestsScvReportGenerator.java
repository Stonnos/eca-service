package com.ecaservice.auto.test.report;

import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.test.common.report.AbstractCsvTestResultsReportGenerator;
import com.ecaservice.test.common.report.TestResultsCounter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Abstract auto tests report generator service.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAutoTestsScvReportGenerator
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
}
