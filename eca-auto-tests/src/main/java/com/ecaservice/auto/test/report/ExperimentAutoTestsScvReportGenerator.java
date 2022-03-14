package com.ecaservice.auto.test.report;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.zip.ZipOutputStream;

import static com.ecaservice.test.common.util.Utils.totalTime;
import static com.ecaservice.test.common.util.ZipUtils.writeAndFlushNextEntry;

/**
 * Experiment auto tests report generator service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentAutoTestsScvReportGenerator
        extends AbstractAutoTestsScvReportGenerator<ExperimentRequestEntity> {

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

    private static final String EXPERIMENT_RESULTS_JSON_FILE_NAME_FORMAT = "experiment-results-%s-(%d).json";

    private final ObjectMapper objectMapper;
    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Constructor with spring dependencies injection.
     *
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param autoTestsProperties             - auto test properties
     * @param objectMapper                    - object mapper
     * @param experimentRequestRepository     - experiment request repository
     */
    public ExperimentAutoTestsScvReportGenerator(BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                                 AutoTestsProperties autoTestsProperties,
                                                 ObjectMapper objectMapper,
                                                 ExperimentRequestRepository experimentRequestRepository) {
        super(AutoTestType.EXPERIMENT_REQUEST_PROCESS, autoTestsProperties, baseEvaluationRequestRepository);
        this.objectMapper = objectMapper;
        this.experimentRequestRepository = experimentRequestRepository;
    }

    @Override
    protected String[] getResultsReportHeaders() {
        return TEST_RESULTS_HEADERS;
    }

    @Override
    protected Page<ExperimentRequestEntity> getNextPage(AutoTestsJobEntity autoTestsJobEntity, Pageable pageable) {
        return experimentRequestRepository.findAllByJob(autoTestsJobEntity, pageable);
    }

    @Override
    protected void printCsvRecord(CSVPrinter printer, ExperimentRequestEntity experimentRequestEntity)
            throws IOException {
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

    @Override
    protected void printAdditionalRecord(ZipOutputStream zipOutputStream,
                                         OutputStreamWriter outputStreamWriter,
                                         ExperimentRequestEntity experimentRequestEntity) throws IOException {
        if (!CollectionUtils.isEmpty(experimentRequestEntity.getExperimentResultDetails())) {
            for (var evaluationResultsDetailsMatch : experimentRequestEntity.getExperimentResultDetails()) {
                String fileName =
                        String.format(EXPERIMENT_RESULTS_JSON_FILE_NAME_FORMAT, experimentRequestEntity.getRequestId(),
                                evaluationResultsDetailsMatch.getResultIndex());
                String json =
                        objectMapper.writeValueAsString(evaluationResultsDetailsMatch);
                writeAndFlushNextEntry(zipOutputStream, outputStreamWriter, fileName, json);
            }
        }
    }
}
