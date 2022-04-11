package com.ecaservice.auto.test.report;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationResultsTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.TestStepVisitor;
import com.ecaservice.auto.test.exception.ReportProcessingException;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.repository.autotest.EvaluationRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ecaservice.test.common.util.Utils.totalTime;
import static com.ecaservice.test.common.util.ZipUtils.flush;
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

    private static final String[] EVALUATION_REQUEST_TEST_RESULTS_HEADERS = {
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
    private static final String EVALUATION_RESULT_TOTAL_REPORT_CSV_FILE_NAME_FORMAT =
            "test-evaluation-result-total-report-%s.csv";

    private final ObjectMapper objectMapper;
    private final EvaluationRequestRepository evaluationRequestRepository;
    private final BaseTestStepRepository baseTestStepRepository;

    /**
     * Constructor with spring dependencies injection.
     *
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param autoTestsProperties             - auto test properties
     * @param objectMapper                    - object mapper
     * @param evaluationRequestRepository     - evaluation request repository
     * @param baseTestStepRepository          - base test step repository
     */
    public EvaluationAutoTestsScvReportGenerator(BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                                 AutoTestsProperties autoTestsProperties,
                                                 ObjectMapper objectMapper,
                                                 EvaluationRequestRepository evaluationRequestRepository,
                                                 BaseTestStepRepository baseTestStepRepository) {
        super(AutoTestType.EVALUATION_REQUEST_PROCESS, autoTestsProperties, baseEvaluationRequestRepository);
        this.objectMapper = objectMapper;
        this.evaluationRequestRepository = evaluationRequestRepository;
        this.baseTestStepRepository = baseTestStepRepository;
    }

    @Override
    protected String[] getResultsReportHeaders() {
        return EVALUATION_REQUEST_TEST_RESULTS_HEADERS;
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
                                         EvaluationRequestEntity evaluationRequestEntity) {
        var testSteps = baseTestStepRepository.findAllByEvaluationRequestEntity(evaluationRequestEntity);
        if (CollectionUtils.isEmpty(testSteps)) {
            log.info("No one test step found for request [{}] report", evaluationRequestEntity.getRequestId());
        } else {
            log.info("Found [{}] test steps for request [{}] report", testSteps.size(),
                    evaluationRequestEntity.getRequestId());
            for (var testStep : testSteps) {
                testStep.visit(new TestStepVisitor() {
                    @Override
                    public void visit(EvaluationResultsTestStepEntity evaluationResultsTestStepEntity) {
                        try {
                            printEvaluationResultsTestResultsCsvReport(zipOutputStream, outputStreamWriter,
                                    evaluationResultsTestStepEntity);
                            printEvaluationResultsDetailsJson(zipOutputStream, outputStreamWriter,
                                    evaluationResultsTestStepEntity);
                        } catch (IOException ex) {
                            throw new ReportProcessingException(ex.getMessage());
                        }
                    }
                });
            }
        }
    }

    private void printEvaluationResultsTestResultsCsvReport(ZipOutputStream zipOutputStream,
                                                            OutputStreamWriter outputStreamWriter,
                                                            EvaluationResultsTestStepEntity testStep)
            throws IOException {
        String fileName = String.format(EVALUATION_RESULT_TOTAL_REPORT_CSV_FILE_NAME_FORMAT,
                testStep.getEvaluationRequestEntity().getRequestId());
        log.info("Starting to write file [{}] into zip archive", fileName);
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        var printer = new CSVPrinter(outputStreamWriter,
                CSVFormat.EXCEL.withHeader(getResultsReportHeaders()).withDelimiter(getHeaderDelimiter()));
        printer.printRecord(Arrays.asList(
                testStep.getEvaluationRequestEntity().getClassifierName(),
                testStep.getEvaluationRequestEntity().getRequestId(),
                testStep.getEvaluationRequestEntity().getClassifierOptions(),
                testStep.getEvaluationRequestEntity().getEvaluationMethod().getDescription(),
                testStep.getEvaluationRequestEntity().getNumFolds(),
                testStep.getEvaluationRequestEntity().getNumTests(),
                testStep.getEvaluationRequestEntity().getSeed(),
                testStep.getStarted(),
                testStep.getFinished(),
                totalTime(testStep.getStarted(), testStep.getFinished()),
                testStep.getTestResult(),
                testStep.getExecutionStatus(),
                testStep.getTotalMatched(),
                testStep.getTotalNotMatched(),
                testStep.getTotalNotFound(),
                testStep.getEvaluationRequestEntity().getRelationName(),
                testStep.getEvaluationRequestEntity().getNumInstances(),
                testStep.getEvaluationRequestEntity().getNumAttributes(),
                testStep.getDetails()
        ));
        flush(zipOutputStream, outputStreamWriter);
        log.info("File [{}] has been written into zip archive", fileName);
    }

    private void printEvaluationResultsDetailsJson(ZipOutputStream zipOutputStream,
                                                   OutputStreamWriter outputStreamWriter,
                                                   EvaluationResultsTestStepEntity evaluationResultsTestStepEntity)
            throws IOException {
        if (Objects.nonNull(evaluationResultsTestStepEntity.getEvaluationResultsDetails())) {
            String fileName = String.format(EVALUATION_RESULTS_JSON_FILE_NAME_FORMAT,
                    evaluationResultsTestStepEntity.getEvaluationRequestEntity().getRequestId());
            String json =
                    objectMapper.writeValueAsString(evaluationResultsTestStepEntity.getEvaluationResultsDetails());
            writeAndFlushNextEntry(zipOutputStream, outputStreamWriter, fileName, json);
        }
    }
}
