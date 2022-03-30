package com.ecaservice.auto.test.report;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.EmailTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentResultsTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.TestStepVisitor;
import com.ecaservice.auto.test.exception.ReportProcessingException;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.BaseTestStepRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ecaservice.test.common.util.Utils.totalTime;
import static com.ecaservice.test.common.util.ZipUtils.flush;
import static com.ecaservice.test.common.util.ZipUtils.writeAndFlushNextEntry;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Experiment auto tests report generator service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class ExperimentAutoTestsScvReportGenerator
        extends AbstractAutoTestsScvReportGenerator<ExperimentRequestEntity> {

    private static final String[] EXPERIMENT_REQUEST_TEST_RESULTS_HEADERS = {
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
            "details"
    };

    private static final String[] EMAIL_STEP_TEST_RESULTS_HEADERS = {
            "experiment type",
            "experiment request id",
            "email type",
            "email received",
            "started",
            "finished",
            "total time",
            "test result",
            "execution status",
            "total matched",
            "total not matched",
            "total not found",
            "expected download url",
            "actual download url",
            "download url match result",
            "details"
    };

    private static final String EXPERIMENT_RESULTS_JSON_FILE_NAME_FORMAT = "experiment-results-%s-(%d).json";
    private static final String EXPERIMENT_RESULT_TOTAL_REPORT_CSV_FILE_NAME_FORMAT =
            "test-experiment-result-total-report-%s.csv";
    private static final String EXPERIMENT_EMAILS_TOTAL_REPORT_CSV_FILE_NAME_FORMAT =
            "test-experiment-emails-total-report-%s.csv";

    private final ObjectMapper objectMapper;
    private final ExperimentRequestRepository experimentRequestRepository;
    private final BaseTestStepRepository baseTestStepRepository;

    /**
     * Constructor with spring dependencies injection.
     *
     * @param baseEvaluationRequestRepository - base evaluation request repository
     * @param autoTestsProperties             - auto test properties
     * @param objectMapper                    - object mapper
     * @param experimentRequestRepository     - experiment request repository
     * @param baseTestStepRepository          - base test step repository
     */
    public ExperimentAutoTestsScvReportGenerator(BaseEvaluationRequestRepository baseEvaluationRequestRepository,
                                                 AutoTestsProperties autoTestsProperties,
                                                 ObjectMapper objectMapper,
                                                 ExperimentRequestRepository experimentRequestRepository,
                                                 BaseTestStepRepository baseTestStepRepository) {
        super(AutoTestType.EXPERIMENT_REQUEST_PROCESS, autoTestsProperties, baseEvaluationRequestRepository);
        this.objectMapper = objectMapper;
        this.experimentRequestRepository = experimentRequestRepository;
        this.baseTestStepRepository = baseTestStepRepository;
    }

    @Override
    protected String[] getResultsReportHeaders() {
        return EXPERIMENT_REQUEST_TEST_RESULTS_HEADERS;
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
                experimentRequestEntity.getDetails()
        ));
    }

    @Override
    protected void printAdditionalRecord(ZipOutputStream zipOutputStream,
                                         OutputStreamWriter outputStreamWriter,
                                         ExperimentRequestEntity experimentRequestEntity) {
        var testSteps = baseTestStepRepository.findAllByEvaluationRequestEntity(experimentRequestEntity);
        if (CollectionUtils.isEmpty(testSteps)) {
            log.info("No one test step found for request [{}] report", experimentRequestEntity.getRequestId());
        } else {
            log.info("Found [{}] test steps for request [{}] report", testSteps.size(),
                    experimentRequestEntity.getRequestId());
            List<EmailTestStepEntity> emailTestSteps = newArrayList();
            for (var testStep : testSteps) {
                testStep.visit(new TestStepVisitor() {
                    @Override
                    public void visit(EmailTestStepEntity emailTestStepEntity) {
                        emailTestSteps.add(emailTestStepEntity);
                    }

                    @Override
                    public void visit(ExperimentResultsTestStepEntity experimentResultsTestStepEntity) {
                        try {
                            printExperimentResultsTestResultsCsvReport(zipOutputStream, outputStreamWriter,
                                    experimentResultsTestStepEntity);
                            printExperimentResultsDetailsJson(zipOutputStream, outputStreamWriter,
                                    experimentResultsTestStepEntity);
                        } catch (IOException ex) {
                            throw new ReportProcessingException(ex.getMessage());
                        }
                    }
                });
            }
            if (!CollectionUtils.isEmpty(emailTestSteps)) {
                printEmailTestsResultsCsvReport(zipOutputStream, outputStreamWriter, experimentRequestEntity,
                        emailTestSteps);
            }
        }
    }

    private void printExperimentResultsTestResultsCsvReport(ZipOutputStream zipOutputStream,
                                                            OutputStreamWriter outputStreamWriter,
                                                            ExperimentResultsTestStepEntity testStep)
            throws IOException {
        String fileName = String.format(EXPERIMENT_RESULT_TOTAL_REPORT_CSV_FILE_NAME_FORMAT,
                testStep.getEvaluationRequestEntity().getRequestId());
        log.info("Starting to write file [{}] into zip archive", fileName);
        zipOutputStream.putNextEntry(new ZipEntry(fileName));
        var printer = new CSVPrinter(outputStreamWriter,
                CSVFormat.EXCEL.withHeader(getResultsReportHeaders()).withDelimiter(getHeaderDelimiter()));
        printer.printRecord(Arrays.asList(
                testStep.getEvaluationRequestEntity().getExperimentType().getDescription(),
                testStep.getEvaluationRequestEntity().getRequestId(),
                testStep.getEvaluationRequestEntity().getEvaluationMethod().getDescription(),
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

    private void printExperimentResultsDetailsJson(ZipOutputStream zipOutputStream,
                                                   OutputStreamWriter outputStreamWriter,
                                                   ExperimentResultsTestStepEntity experimentResultsTestStepEntity)
            throws IOException {
        if (!CollectionUtils.isEmpty(experimentResultsTestStepEntity.getExperimentResultDetails())) {
            for (var evaluationResultsDetailsMatch : experimentResultsTestStepEntity.getExperimentResultDetails()) {
                String fileName = String.format(EXPERIMENT_RESULTS_JSON_FILE_NAME_FORMAT,
                        experimentResultsTestStepEntity.getEvaluationRequestEntity().getRequestId(),
                        evaluationResultsDetailsMatch.getResultIndex());
                String json = objectMapper.writeValueAsString(evaluationResultsDetailsMatch);
                writeAndFlushNextEntry(zipOutputStream, outputStreamWriter, fileName, json);
            }
        }
    }

    private void printEmailTestsResultsCsvReport(ZipOutputStream zipOutputStream,
                                                 OutputStreamWriter outputStreamWriter,
                                                 ExperimentRequestEntity experimentRequestEntity,
                                                 List<EmailTestStepEntity> testSteps) {
        try {
            String fileName = String.format(EXPERIMENT_EMAILS_TOTAL_REPORT_CSV_FILE_NAME_FORMAT,
                    experimentRequestEntity.getRequestId());
            log.info("Starting to write file [{}] into zip archive", fileName);
            zipOutputStream.putNextEntry(new ZipEntry(fileName));
            var printer = new CSVPrinter(outputStreamWriter,
                    CSVFormat.EXCEL.withHeader(EMAIL_STEP_TEST_RESULTS_HEADERS).withDelimiter(getHeaderDelimiter()));
            for (var testStep : testSteps) {
                printer.printRecord(Arrays.asList(
                        testStep.getEvaluationRequestEntity().getExperimentType().getDescription(),
                        testStep.getEvaluationRequestEntity().getRequestId(),
                        testStep.getEmailType().getDescription(),
                        testStep.isMessageReceived(),
                        testStep.getStarted(),
                        testStep.getFinished(),
                        totalTime(testStep.getStarted(), testStep.getFinished()),
                        testStep.getTestResult(),
                        testStep.getExecutionStatus(),
                        testStep.getTotalMatched(),
                        testStep.getTotalNotMatched(),
                        testStep.getTotalNotFound(),
                        testStep.getExpectedDownloadUrl(),
                        testStep.getActualDownloadUrl(),
                        testStep.getDownloadUrlMatchResult(),
                        testStep.getDetails()
                ));
            }
            flush(zipOutputStream, outputStreamWriter);
            log.info("File [{}] has been written into zip archive", fileName);
        } catch (IOException ex) {
            throw new ReportProcessingException(ex.getMessage());
        }
    }
}
