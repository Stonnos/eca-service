package com.ecaservice.external.api.test.report;

import com.ecaservice.external.api.test.config.ExternalApiTestsConfig;
import com.ecaservice.external.api.test.dto.AutoTestType;
import com.ecaservice.external.api.test.entity.ExperimentRequestAutoTestEntity;
import com.ecaservice.external.api.test.entity.JobEntity;
import com.ecaservice.external.api.test.repository.ExperimentRequestAutoTestRepository;
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
 * Experiment api auto tests csv report generator.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentApiTestResultsCsvReportGenerator extends ExternalApiTestResultsCsvReportGenerator {

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
            "expected num models",
            "actual num models",
            "Num models match result",
            "request",
            "response",
            "details"
    };

    private final ExternalApiTestsConfig externalApiTestsConfig;
    private final ExperimentRequestAutoTestRepository experimentRequestAutoTestRepository;

    /**
     * Constructor with parameters.
     *
     * @param externalApiTestsConfig              - external api tests config
     * @param experimentRequestAutoTestRepository - experiment request auto test repository
     */
    public ExperimentApiTestResultsCsvReportGenerator(ExternalApiTestsConfig externalApiTestsConfig,
                                                      ExperimentRequestAutoTestRepository experimentRequestAutoTestRepository) {
        super(AutoTestType.EXPERIMENT_REQUEST_PROCESS);
        this.externalApiTestsConfig = externalApiTestsConfig;
        this.experimentRequestAutoTestRepository = experimentRequestAutoTestRepository;
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
        Page<ExperimentRequestAutoTestEntity> page;
        do {
            page = experimentRequestAutoTestRepository.findAllByJob(jobEntity, pageRequest);
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
                            autoTestEntity.getExpectedNumModels(),
                            autoTestEntity.getActualNumModels(),
                            autoTestEntity.getNumModelsMatchResult(),
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
