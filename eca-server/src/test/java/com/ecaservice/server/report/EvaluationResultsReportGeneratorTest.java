package com.ecaservice.server.report;

import com.ecaservice.classifier.options.config.ClassifiersOptionsConfig;
import com.ecaservice.classifier.template.processor.config.ClassifiersTemplateProperties;
import com.ecaservice.classifier.template.processor.service.ClassifierOptionsProcessor;
import com.ecaservice.classifier.template.processor.service.ClassifiersTemplateProvider;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.mapping.EvaluationResultsReportDataMapperImpl;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.report.model.ClassificationCostsReportBean;
import com.ecaservice.server.report.model.EvaluationResultsReportBean;
import com.ecaservice.server.report.model.EvaluationResultsReportInputData;
import com.ecaservice.server.service.classifiers.ClassifierOptionsInfoProcessor;
import com.ecaservice.server.service.classifiers.ClassifiersFormTemplateProvider;
import com.ecaservice.server.service.evaluation.ConfusionMatrixService;
import com.ecaservice.web.dto.model.FormTemplateGroupDto;
import lombok.Cleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.stream.IntStream;

import static com.ecaservice.report.ReportGenerator.generateReport;
import static com.ecaservice.server.TestHelperUtils.createEvaluationLog;
import static com.ecaservice.server.TestHelperUtils.createStackingOptions;
import static com.ecaservice.server.TestHelperUtils.loadClassifiersTemplates;
import static com.ecaservice.server.TestHelperUtils.loadEnsembleClassifiersTemplates;
import static com.ecaservice.server.TestHelperUtils.loadEvaluationResultsResponse;
import static com.ecaservice.server.report.ReportTemplates.EVALUATION_RESULTS_TEMPLATE;
import static com.ecaservice.server.util.ClassifierOptionsHelper.toJsonString;
import static org.apache.commons.lang3.SystemUtils.USER_DIR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link EvaluationResultsReportDataProcessor} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({ClassifiersFormTemplateProvider.class, ClassifierOptionsInfoProcessor.class, ClassifiersOptionsConfig.class,
        ClassifiersProperties.class, ClassifierOptionsProcessor.class, ClassifiersTemplateProperties.class,
        ClassifiersTemplateProvider.class, EvaluationResultsReportDataProcessor.class,
        EvaluationResultsReportDataMapperImpl.class, ConfusionMatrixService.class})
public class EvaluationResultsReportGeneratorTest {

    private static final String CLASSIFIERS = "classifiers";
    private static final String ENSEMBLE_CLASSIFIERS = "ensembleClassifiers";

    @MockBean
    private FormTemplateProvider formTemplateProvider;

    @Autowired
    private EvaluationResultsReportDataProcessor evaluationResultsReportDataProcessor;

    private GetEvaluationResultsResponse evaluationResultsResponse;

    @BeforeEach
    void init() {
        evaluationResultsResponse = loadEvaluationResultsResponse();
        FormTemplateGroupDto templates = loadClassifiersTemplates();
        FormTemplateGroupDto ensembleTemplates = loadEnsembleClassifiersTemplates();
        when(formTemplateProvider.getFormGroupDto(CLASSIFIERS)).thenReturn(templates);
        when(formTemplateProvider.getFormGroupDto(ENSEMBLE_CLASSIFIERS)).thenReturn(ensembleTemplates);
    }

    @Test
    void testGenerateEvaluationResultsReport() throws IOException {
        EvaluationLog evaluationLog = createEvaluationLog();
        var stackingOptions = createStackingOptions();
        evaluationLog.setClassifierOptions(toJsonString(stackingOptions));

        EvaluationResultsReportInputData evaluationResultsReportInputData = EvaluationResultsReportInputData.builder()
                .evaluationEntity(evaluationLog)
                .classifierOptions(evaluationLog.getClassifierOptions())
                .evaluationResultsResponse(evaluationResultsResponse)
                .build();
        EvaluationResultsReportBean evaluationResultsReportBean =
                evaluationResultsReportDataProcessor.processReportData(evaluationResultsReportInputData);
        verifyEvaluationResultsReport(evaluationResultsReportBean, evaluationLog);
        testGenerateReport(evaluationResultsReportBean);
    }

    private void verifyEvaluationResultsReport(EvaluationResultsReportBean evaluationResultsReportBean,
                                               EvaluationLog evaluationLog) {
        assertThat(evaluationResultsReportBean).isNotNull();
        assertThat(evaluationResultsReportBean.getRequestId()).isEqualTo(evaluationLog.getRequestId());
        assertThat(evaluationResultsReportBean.getClassifierInfo()).isNotNull();
        assertThat(evaluationResultsReportBean.getRelationName()).isEqualTo(
                evaluationLog.getInstancesInfo().getRelationName());
        assertThat(evaluationResultsReportBean.getNumInstances()).isEqualTo(
                evaluationLog.getInstancesInfo().getNumInstances());
        assertThat(evaluationResultsReportBean.getNumAttributes()).isEqualTo(
                evaluationLog.getInstancesInfo().getNumAttributes());
        assertThat(evaluationResultsReportBean.getNumClasses()).isEqualTo(
                evaluationLog.getInstancesInfo().getNumClasses());
        assertThat(evaluationResultsReportBean.getClassName()).isEqualTo(
                evaluationLog.getInstancesInfo().getClassName());
        assertThat(evaluationResultsReportBean.getEvaluationMethod()).isNotNull();
        assertThat(evaluationResultsReportBean.getClassValues()).hasSameSizeAs(
                evaluationResultsResponse.getClassificationCosts());
        assertThat(evaluationResultsReportBean.getConfusionMatrixCells()).isNotEmpty();
        assertThat(evaluationResultsReportBean.getNumTestInstances()).isEqualTo(
                evaluationResultsResponse.getStatistics().getNumTestInstances());
        assertThat(evaluationResultsReportBean.getNumCorrect()).isEqualTo(
                evaluationResultsResponse.getStatistics().getNumCorrect());
        assertThat(evaluationResultsReportBean.getNumIncorrect()).isEqualTo(
                evaluationResultsResponse.getStatistics().getNumIncorrect());
        assertThat(evaluationResultsReportBean.getPctCorrect()).isEqualTo(
                evaluationResultsResponse.getStatistics().getPctCorrect());
        assertThat(evaluationResultsReportBean.getPctIncorrect()).isEqualTo(
                evaluationResultsResponse.getStatistics().getPctIncorrect());
        assertThat(evaluationResultsReportBean.getMeanAbsoluteError()).isEqualTo(
                evaluationResultsResponse.getStatistics().getMeanAbsoluteError());
        assertThat(evaluationResultsReportBean.getRootMeanSquaredError()).isEqualTo(
                evaluationResultsResponse.getStatistics().getRootMeanSquaredError());
        assertThat(evaluationResultsReportBean.getConfidenceIntervalLowerBound()).isEqualTo(
                evaluationResultsResponse.getStatistics().getConfidenceIntervalLowerBound());
        assertThat(evaluationResultsReportBean.getConfidenceIntervalUpperBound()).isEqualTo(
                evaluationResultsResponse.getStatistics().getConfidenceIntervalUpperBound());

        verifyClassificationCosts(evaluationResultsReportBean);
    }

    private void verifyClassificationCosts(EvaluationResultsReportBean evaluationResultsReportBean) {
        assertThat(evaluationResultsReportBean.getClassificationCosts()).hasSameSizeAs(
                evaluationResultsResponse.getClassificationCosts());
        IntStream.range(0, evaluationResultsResponse.getClassificationCosts().size()).forEach(i -> {
            ClassificationCostsReport expected = evaluationResultsResponse.getClassificationCosts().get(i);
            ClassificationCostsReportBean actual = evaluationResultsReportBean.getClassificationCosts().get(i);
            assertThat(actual.getAucValue()).isEqualTo(expected.getRocCurve().getAucValue());
            assertThat(actual.getClassValue()).isEqualTo(expected.getClassValue());
            assertThat(actual.getTrueNegativeRate()).isEqualTo(expected.getTrueNegativeRate());
            assertThat(actual.getTruePositiveRate()).isEqualTo(expected.getTruePositiveRate());
            assertThat(actual.getFalseNegativeRate()).isEqualTo(expected.getFalseNegativeRate());
            assertThat(actual.getFalsePositiveRate()).isEqualTo(expected.getFalsePositiveRate());
        });
    }

    private void testGenerateReport(EvaluationResultsReportBean evaluationResultsReportBean) throws IOException {
        String fileName = String.format("%s/target/evaluation-results-report.xlsx", USER_DIR);
        @Cleanup var outputStream = new FileOutputStream(fileName);
        generateReport(EVALUATION_RESULTS_TEMPLATE, evaluationResultsReportBean, outputStream);
    }
}
