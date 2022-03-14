package com.ecaservice.auto.test.report;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
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
 * Experiment auto tests report generator service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentAutoTestsScvReportGenerator extends AbstractAutoTestsScvReportGenerator {

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
            "details"
    };

    private final AutoTestsProperties autoTestsProperties;
    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Constructor with spring dependencies injection.
     *
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param autoTestsProperties             - auto test properties
     * @param experimentRequestRepository     - experiment request repository
     */
    public ExperimentAutoTestsScvReportGenerator(BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                                 AutoTestsProperties autoTestsProperties,
                                                 ExperimentRequestRepository experimentRequestRepository) {
        super(AutoTestType.EXPERIMENT_REQUEST_PROCESS, baseEvaluationRequestRepository);
        this.autoTestsProperties = autoTestsProperties;
        this.experimentRequestRepository = experimentRequestRepository;
    }

    @Override
    protected String[] getResultsReportHeaders() {
        return TEST_RESULTS_HEADERS;
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
                            experimentRequestEntity.getDetails()
                    ));
                }
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }
}
