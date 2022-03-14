package com.ecaservice.auto.test.report;

import com.ecaservice.auto.test.config.AutoTestsProperties;
import com.ecaservice.auto.test.entity.autotest.AutoTestType;
import com.ecaservice.auto.test.entity.autotest.AutoTestsJobEntity;
import com.ecaservice.auto.test.entity.autotest.EvaluationRequestEntity;
import com.ecaservice.auto.test.repository.autotest.BaseEvaluationRequestRepository;
import com.ecaservice.auto.test.repository.autotest.EvaluationRequestRepository;
import com.ecaservice.test.common.report.TestResultsCounter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.ecaservice.test.common.util.Utils.totalTime;

/**
 * Evaluation auto tests report generator service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
public class EvaluationAutoTestsScvReportGenerator extends AbstractAutoTestsScvReportGenerator {

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

    private final AutoTestsProperties autoTestsProperties;
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
        super(AutoTestType.EVALUATION_REQUEST_PROCESS, baseEvaluationRequestRepository);
        this.autoTestsProperties = autoTestsProperties;
        this.objectMapper = objectMapper;
        this.evaluationRequestRepository = evaluationRequestRepository;
    }

    @Override
    protected String[] getResultsReportHeaders() {
        return TEST_RESULTS_HEADERS;
    }

    @Override
    protected void printReportTestResults(CSVPrinter printer, AutoTestsJobEntity jobEntity,
                                          TestResultsCounter testResultsCounter) throws IOException {
        Pageable pageRequest = PageRequest.of(0, autoTestsProperties.getPageSize());
        Page<EvaluationRequestEntity> page;
        do {
            page = evaluationRequestRepository.findAllByJob(jobEntity, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one entity has been fetched");
                break;
            } else {
                for (var evaluationRequestEntity : page.getContent()) {
                    evaluationRequestEntity.getTestResult().apply(testResultsCounter);
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
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }

    @Override
    protected void printAdditionalReportData(ZipOutputStream zipOutputStream,
                                             OutputStreamWriter outputStreamWriter,
                                             AutoTestsJobEntity autoTestsJobEntity) throws IOException {
        Pageable pageRequest = PageRequest.of(0, autoTestsProperties.getPageSize());
        Page<EvaluationRequestEntity> page;
        do {
            page = evaluationRequestRepository.findAllByJob(autoTestsJobEntity, pageRequest);
            if (page == null || !page.hasContent()) {
                log.trace("No one entity has been fetched");
                break;
            } else {
                for (var evaluationRequestEntity : page.getContent()) {
                    if (Objects.nonNull(evaluationRequestEntity.getEvaluationResultsDetails())) {
                        String fileName = String.format(EVALUATION_RESULTS_JSON_FILE_NAME_FORMAT,
                                evaluationRequestEntity.getRequestId());
                        log.info("Starting to write file [{}] into zip archive", fileName);
                        zipOutputStream.putNextEntry(new ZipEntry(fileName));
                        String json =
                                objectMapper.writeValueAsString(evaluationRequestEntity.getEvaluationResultsDetails());
                        outputStreamWriter.write(json);
                        flush(outputStreamWriter, zipOutputStream);
                        log.info("File [{}] has been written into zip archive", fileName);
                    }
                }
            }
            pageRequest = page.nextPageable();
        } while (page.hasNext());
    }
}
