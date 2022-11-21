package com.ecaservice.external.api.test.report;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.dto.AutoTestType;
import com.ecaservice.external.api.test.entity.EvaluationRequestAutoTestEntity;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.EvaluationRequestAutoTestRepository;
import com.ecaservice.test.common.report.TestResultsCounter;
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
 * Evaluation api auto tests csv report generator.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationApiTestResultsCsvReportGenerator extends ExternalApiTestResultsCsvReportGenerator {

    private static final String[] TEST_RESULTS_HEADERS = {
            "Display name",
            "started",
            "finished",
            "total time",
            "result",
            "total matched",
            "total not matched",
            "total not found",
            "expected response code",
            "actual response code",
            "response code match result",
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

    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final EvaluationRequestAutoTestRepository evaluationRequestAutoTestRepository;

    /**
     * Constructor with parameters.
     *
     * @param externalApiTestsConfig              - external api tests config
     * @param evaluationRequestAutoTestRepository - evaluation request auto test repository
     */
    public EvaluationApiTestResultsCsvReportGenerator(ExternalApiTestsConfig externalApiTestsConfig,
                                                      EvaluationRequestAutoTestRepository evaluationRequestAutoTestRepository) {
        super(AutoTestType.EVALUATION_REQUEST_PROCESS);
        this.externalApiTestsConfig = externalApiTestsConfig;
        this.evaluationRequestAutoTestRepository = evaluationRequestAutoTestRepository;
    }

    @Override
    protected String[] getResultsReportHeaders() {
        return TEST_RESULTS_HEADERS;
    }

    @Override
    protected void printReportTestResults(CSVPrinter printer,
                                          JobEntity jobEntity,
                                          TestResultsCounter testResultsCounter)
            throws IOException {
        Pageable pageRequest = PageRequest.of(0, externalApiTestsConfig.getPageSize());
        Page<EvaluationRequestAutoTestEntity> page;
        do {
            page = evaluationRequestAutoTestRepository.findAllByJob(jobEntity, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one entity has been fetched");
                break;
            } else {
                for (var autoTestEntity : page.getContent()) {
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
                            autoTestEntity.getExpectedResponseCode(),
                            autoTestEntity.getActualResponseCode(),
                            autoTestEntity.getResponseCodeMatchResult(),
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
}
