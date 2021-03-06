package com.ecaservice.service.evaluation;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.classifier.options.config.ClassifiersOptionsConfiguration;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.ConfusionMatrixReport;
import com.ecaservice.ers.dto.EnsembleClassifierReport;
import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.ers.dto.RocCurveReport;
import com.ecaservice.ers.dto.StatisticsReport;
import com.ecaservice.mapping.InstancesConverter;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import eca.metrics.KNearestNeighbours;
import eca.regression.Logistic;
import eca.trees.CART;
import eca.trees.J48;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for checking {@link EvaluationResultsService} functionality.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({EvaluationResultsService.class, CrossValidationConfig.class,
        InstancesConverter.class, ClassifiersOptionsConfiguration.class})
class EvaluationResultsServiceTest {

    @Inject
    private EvaluationResultsService evaluationResultsService;
    @Inject
    private CrossValidationConfig crossValidationConfig;

    private EvaluationResults evaluationResults;

    @BeforeEach
    void init() {
        evaluationResults = TestHelperUtils.getEvaluationResults();
    }

    @Test
    void testMapEvaluationResults() {
        EvaluationResultsRequest resultsRequest = evaluationResultsService.proceed(evaluationResults);
        assertThat(resultsRequest).isNotNull();
        Instances instances = evaluationResults.getEvaluation().getData();
        Evaluation evaluation = evaluationResults.getEvaluation();
        verifyInstancesReport(instances, resultsRequest);
        verifyClassifiersReport(resultsRequest);
        verifyEvaluationMethodReport(evaluation, resultsRequest);
        verifyClassificationCostsReport(evaluation, resultsRequest);
        verifyConfusionMatrixReport(evaluation, resultsRequest);
        verifyStatisticsReport(evaluation, resultsRequest);
    }

    @Test
    void testHeterogeneousClassifierMap() {
        HeterogeneousClassifier heterogeneousClassifier = new HeterogeneousClassifier();
        heterogeneousClassifier.setClassifiersSet(new ClassifiersSet());
        heterogeneousClassifier.getClassifiersSet().addClassifier(new CART());
        heterogeneousClassifier.getClassifiersSet().addClassifier(new Logistic());
        heterogeneousClassifier.getClassifiersSet().addClassifier(new KNearestNeighbours());
        EvaluationResults results = new EvaluationResults(heterogeneousClassifier, evaluationResults.getEvaluation());
        EvaluationResultsRequest resultsRequest = evaluationResultsService.proceed(results);
        assertThat(resultsRequest.getClassifierReport()).isNotNull();
        assertThat(resultsRequest.getClassifierReport()).isInstanceOf(EnsembleClassifierReport.class);
        EnsembleClassifierReport classifierReport = (EnsembleClassifierReport) resultsRequest.getClassifierReport();
        assertThat(classifierReport.getIndividualClassifiers().size()).isEqualTo(
                heterogeneousClassifier.getClassifiersSet().size());
    }

    @Test
    void testStackingClassifierMap() {
        StackingClassifier stackingClassifier = new StackingClassifier();
        stackingClassifier.setClassifiers(new ClassifiersSet());
        stackingClassifier.getClassifiers().addClassifier(new CART());
        stackingClassifier.getClassifiers().addClassifier(new Logistic());
        stackingClassifier.getClassifiers().addClassifier(new KNearestNeighbours());
        stackingClassifier.setMetaClassifier(new J48());
        EvaluationResults results = new EvaluationResults(stackingClassifier, evaluationResults.getEvaluation());
        EvaluationResultsRequest resultsRequest = evaluationResultsService.proceed(results);
        assertThat(resultsRequest.getClassifierReport()).isNotNull();
        assertThat(resultsRequest.getClassifierReport()).isInstanceOf(EnsembleClassifierReport.class);
        EnsembleClassifierReport classifierReport = (EnsembleClassifierReport) resultsRequest.getClassifierReport();
        assertThat(classifierReport.getIndividualClassifiers().size()).isEqualTo(
                stackingClassifier.getClassifiers().size() + 1);
        ClassifierReport metaClassifierReport =
                classifierReport.getIndividualClassifiers().get(classifierReport.getIndividualClassifiers().size() - 1);
        assertThat(metaClassifierReport.isMetaClassifier()).isTrue();
    }

    private void verifyInstancesReport(Instances instances, EvaluationResultsRequest resultsRequest) {
        InstancesReport instancesReport = resultsRequest.getInstances();
        assertThat(instancesReport).isNotNull();
        assertThat(instancesReport.getStructure()).isNotNull();
        assertThat(instancesReport.getRelationName()).isEqualTo(instances.relationName());
        assertThat(instancesReport.getClassName()).isEqualTo(instances.classAttribute().name());
        assertThat(instancesReport.getNumInstances().intValue()).isEqualTo(instances.numInstances());
        assertThat(instancesReport.getNumAttributes().intValue()).isEqualTo(instances.numAttributes());
        assertThat(instancesReport.getNumClasses().intValue()).isEqualTo(instances.numClasses());
    }

    private void verifyClassifiersReport(EvaluationResultsRequest resultsRequest) {
        //Classifier report assertion
        ClassifierReport classifierReport = resultsRequest.getClassifierReport();
        AbstractClassifier classifier = (AbstractClassifier) evaluationResults.getClassifier();
        assertThat(classifierReport).isNotNull();
        assertThat(classifierReport.getClassifierName()).isEqualTo(classifier.getClass().getSimpleName());
        assertThat(classifierReport.getClassifierInputOptions()).hasSize(classifier.getOptions().length / 2);
    }

    private void verifyEvaluationMethodReport(Evaluation evaluation, EvaluationResultsRequest resultsRequest) {
        //Evaluation method report assertion
        EvaluationMethodReport evaluationMethodReport = resultsRequest.getEvaluationMethodReport();
        assertThat(evaluationMethodReport).isNotNull();
        assertThat(evaluationMethodReport.getEvaluationMethod()).isEqualTo(
                EvaluationMethod.CROSS_VALIDATION);
        assertThat(evaluationMethodReport.getNumFolds().intValue()).isEqualTo(evaluation.numFolds());
        assertThat(evaluationMethodReport.getNumTests().intValue()).isEqualTo(
                evaluation.getValidationsNum());
        assertThat(evaluationMethodReport.getSeed().intValue()).isEqualTo(crossValidationConfig.getSeed());
    }

    private void verifyClassificationCostsReport(Evaluation evaluation, EvaluationResultsRequest resultsRequest) {
        //Classification costs report assertion
        Instances instances = evaluationResults.getEvaluation().getData();
        List<ClassificationCostsReport> costsReports = resultsRequest.getClassificationCosts();
        assertThat(costsReports).isNotNull();
        assertThat(costsReports.size()).isEqualTo(instances.numClasses());
        Attribute classAttribute = instances.classAttribute();
        for (int i = 0; i < classAttribute.numValues(); i++) {
            assertThat(costsReports.get(i).getClassValue()).isEqualTo(classAttribute.value(i));
            assertThat(costsReports.get(i).getFalseNegativeRate().doubleValue()).isEqualTo(
                    evaluation.falseNegativeRate(i));
            assertThat(costsReports.get(i).getFalsePositiveRate().doubleValue()).isEqualTo(
                    evaluation.falsePositiveRate(i));
            assertThat(costsReports.get(i).getTrueNegativeRate().doubleValue()).isEqualTo(
                    evaluation.trueNegativeRate(i));
            assertThat(costsReports.get(i).getTruePositiveRate().doubleValue()).isEqualTo(
                    evaluation.truePositiveRate(i));
            RocCurveReport rocCurveReport = costsReports.get(i).getRocCurve();
            assertThat(rocCurveReport).isNotNull();
            assertThat(rocCurveReport.getAucValue().doubleValue()).isEqualTo(evaluation.areaUnderROC(i));
            assertThat(rocCurveReport.getSpecificity()).isNotNull();
            assertThat(rocCurveReport.getSensitivity()).isNotNull();
            assertThat(rocCurveReport.getThresholdValue()).isNotNull();
        }
    }

    private void verifyConfusionMatrixReport(Evaluation evaluation, EvaluationResultsRequest resultsRequest) {
        Instances instances = evaluationResults.getEvaluation().getData();
        Attribute classAttribute = instances.classAttribute();
        //Confusion matrix report assertion
        List<ConfusionMatrixReport> confusionMatrixReports = resultsRequest.getConfusionMatrix();
        assertThat(confusionMatrixReports).isNotNull();
        double[][] confusionMatrix = evaluation.confusionMatrix();
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[i].length; j++) {
                ConfusionMatrixReport confusionMatrixReport =
                        confusionMatrixReports.get(i * confusionMatrix[i].length + j);
                assertThat(confusionMatrixReport.getPredictedClass()).isEqualTo(classAttribute.value(j));
                assertThat(confusionMatrixReport.getActualClass()).isEqualTo(classAttribute.value(i));
                assertThat(confusionMatrixReport.getNumInstances().doubleValue()).isEqualTo(
                        confusionMatrix[i][j]);
            }
        }
    }

    private void verifyStatisticsReport(Evaluation evaluation, EvaluationResultsRequest resultsRequest) {
        //Statistics report assertion
        StatisticsReport statisticsReport = resultsRequest.getStatistics();
        assertThat(statisticsReport).isNotNull();
        assertThat(statisticsReport.getNumTestInstances().doubleValue()).isEqualTo(
                evaluation.numInstances());
        assertThat(statisticsReport.getNumCorrect().doubleValue()).isEqualTo(evaluation.correct());
        assertThat(statisticsReport.getNumIncorrect().doubleValue()).isEqualTo(evaluation.incorrect());
        assertThat(statisticsReport.getPctCorrect().doubleValue()).isEqualTo(evaluation.pctCorrect());
        assertThat(statisticsReport.getPctIncorrect().doubleValue()).isEqualTo(evaluation.pctIncorrect());
        assertThat(statisticsReport.getMeanAbsoluteError().doubleValue()).isEqualTo(
                evaluation.meanAbsoluteError());
        assertThat(statisticsReport.getRootMeanSquaredError().doubleValue()).isEqualTo(
                evaluation.rootMeanSquaredError());
        assertThat(statisticsReport.getMaxAucValue().doubleValue()).isEqualTo(evaluation.maxAreaUnderROC());
        assertThat(statisticsReport.getVarianceError().doubleValue()).isEqualTo(evaluation.varianceError());
        double[] confidenceInterval = evaluation.errorConfidenceInterval();
        assertThat(statisticsReport.getConfidenceIntervalLowerBound().doubleValue()).isEqualTo(
                confidenceInterval[0]);
        assertThat(statisticsReport.getConfidenceIntervalUpperBound().doubleValue()).isEqualTo(
                confidenceInterval[1]);
    }
}
