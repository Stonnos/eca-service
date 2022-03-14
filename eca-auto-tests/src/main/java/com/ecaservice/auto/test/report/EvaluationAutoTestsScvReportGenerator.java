package com.ecaservice.auto.test.report;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.EvaluationRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipOutputStream;

import static com.ecaservice.test.common.util.Utils.totalTime;
import static com.ecaservice.test.common.util.ZipUtils.writeAndFlushNextEntry;

/**
 * Evaluation auto tests report generator service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationAutoTestsScvReportGenerator
        extends AbstractAutoTestsScvReportGenerator<EvaluationRequestEntity> {

    private static final String[] TEST_RESULTS_HEADERS = {
            "classifier name",
            "request id",
            "classifier options",
            "evaluation method",
            "folds number",
            "tests number",
            "seed",
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
            "details"
    };

    private static final String EVALUATION_RESULTS_JSON_FILE_NAME_FORMAT = "evaluation-results-%s.json";

    private final ObjectMapper objectMapper;
    private final EvaluationRequestRepository evaluationRequestRepository;

    /**
     * Constructor with spring dependencies injection.
     *
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param autoTestsProperties             - auto test properties
     * @param objectMapper                    - object mapper
     * @param evaluationRequestRepository     - evaluation request repository
     */
    public EvaluationAutoTestsScvReportGenerator(BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                                 AutoTestsProperties autoTestsProperties,
                                                 ObjectMapper objectMapper,
                                                 EvaluationRequestRepository evaluationRequestRepository) {
        super(AutoTestType.EVALUATION_REQUEST_PROCESS, autoTestsProperties, baseEvaluationRequestRepository);
        this.objectMapper = objectMapper;
        this.evaluationRequestRepository = evaluationRequestRepository;
    }

    @Override
    protected String[] getResultsReportHeaders() {
        return TEST_RESULTS_HEADERS;
    }

    @Override
    protected Page<EvaluationRequestEntity> getNextPage(AutoTestsJobEntity autoTestsJobEntity, Pageable pageable) {
        return evaluationRequestRepository.findAllByJob(autoTestsJobEntity, pageable);
    }

    @Override
    protected void printCsvRecord(CSVPrinter printer, EvaluationRequestEntity evaluationRequestEntity)
            throws IOException {
        printer.printRecord(Arrays.asList(
                evaluationRequestEntity.getClassifierName(),
                evaluationRequestEntity.getRequestId(),
                evaluationRequestEntity.getClassifierOptions(),
                evaluationRequestEntity.getEvaluationMethod().getDescription(),
                evaluationRequestEntity.getNumFolds(),
                evaluationRequestEntity.getNumTests(),
                evaluationRequestEntity.getSeed(),
                evaluationRequestEntity.getStarted(),
                evaluationRequestEntity.getFinished(),
                totalTime(evaluationRequestEntity.getStarted(), evaluationRequestEntity.getFinished()),
                evaluationRequestEntity.getTestResult(),
                evaluationRequestEntity.getExecutionStatus(),
                evaluationRequestEntity.getTotalMatched(),
                evaluationRequestEntity.getTotalNotMatched(),
                evaluationRequestEntity.getTotalNotFound(),
                evaluationRequestEntity.getRelationName(),
                evaluationRequestEntity.getNumInstances(),
                evaluationRequestEntity.getNumAttributes(),
                evaluationRequestEntity.getDetails()
        ));
    }

    @Override
    protected void printAdditionalRecord(ZipOutputStream zipOutputStream, OutputStreamWriter outputStreamWriter,
                                         EvaluationRequestEntity evaluationRequestEntity) throws IOException {
        if (Objects.nonNull(evaluationRequestEntity.getEvaluationResultsDetails())) {
            String fileName = String.format(EVALUATION_RESULTS_JSON_FILE_NAME_FORMAT,
                    evaluationRequestEntity.getRequestId());
            String json =
                    objectMapper.writeValueAsString(evaluationRequestEntity.getEvaluationResultsDetails());
            writeAndFlushNextEntry(zipOutputStream, outputStreamWriter, fileName, json);
        }
    }
}
