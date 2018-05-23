package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.ClassificationCostsReport;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.dto.evaluation.ConfusionMatrixReport;
import com.ecaservice.dto.evaluation.EnsembleClassifierReport;
import com.ecaservice.dto.evaluation.EvaluationMethod;
import com.ecaservice.dto.evaluation.EvaluationMethodReport;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.InstancesReport;
import com.ecaservice.dto.evaluation.RocCurveReport;
import com.ecaservice.dto.evaluation.StatisticsReport;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.ClassifiersSet;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.StackingClassifier;
import eca.metrics.KNearestNeighbours;
import eca.regression.Logistic;
import eca.trees.CART;
import eca.trees.J48;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.List;

/**
 * Unit tests fro checking {@link EvaluationResultsMapper} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EvaluationResultsMapperTest {

    @Inject
    private EvaluationResultsMapper evaluationResultsMapper;

    private EvaluationResults evaluationResults;

    @Before
    public void init() throws Exception {
        evaluationResults = TestHelperUtils.getEvaluationResults();
    }

    @Test
    public void testMapEvaluationResults() throws Exception {
        EvaluationResultsRequest resultsRequest = evaluationResultsMapper.map(evaluationResults);
        Assertions.assertThat(resultsRequest).isNotNull();

        InstancesReport instancesReport = resultsRequest.getInstances();
        Instances instances = evaluationResults.getEvaluation().getData();
        Assertions.assertThat(instancesReport).isNotNull();
        Assertions.assertThat(instancesReport.getXmlData()).isNotNull();
        Assertions.assertThat(instancesReport.getRelationName()).isEqualTo(instances.relationName());
        Assertions.assertThat(instancesReport.getClassName()).isEqualTo(instances.classAttribute().name());
        Assertions.assertThat(instancesReport.getNumInstances().intValue()).isEqualTo(instances.numInstances());
        Assertions.assertThat(instancesReport.getNumAttributes().intValue()).isEqualTo(instances.numAttributes());
        Assertions.assertThat(instancesReport.getNumClasses().intValue()).isEqualTo(instances.numClasses());

        ClassifierReport classifierReport = resultsRequest.getClassifierReport();
        AbstractClassifier classifier = (AbstractClassifier) evaluationResults.getClassifier();
        Assertions.assertThat(classifierReport).isNotNull();
        Assertions.assertThat(classifierReport.getClassifierName()).isEqualTo(classifier.getClass().getSimpleName());
        Assertions.assertThat(classifierReport.getInputOptionsMap()).isNotNull();
        Assertions.assertThat(classifierReport.getInputOptionsMap().getEntry()).isNotNull();
        Assertions.assertThat(classifierReport.getInputOptionsMap().getEntry().size()).isEqualTo(
                classifier.getOptions().length / 2);

        EvaluationMethodReport evaluationMethodReport = resultsRequest.getEvaluationMethodReport();
        Assertions.assertThat(evaluationMethodReport).isNotNull();
        Evaluation evaluation = evaluationResults.getEvaluation();
        Assertions.assertThat(evaluationMethodReport.getEvaluationMethod()).isEqualTo(
                EvaluationMethod.CROSS_VALIDATION);
        Assertions.assertThat(evaluationMethodReport.getNumFolds().intValue()).isEqualTo(evaluation.numFolds());
        Assertions.assertThat(evaluationMethodReport.getNumTests().intValue()).isEqualTo(
                evaluation.getValidationsNum());

        List<ClassificationCostsReport> costsReports = resultsRequest.getClassificationCosts();
        Assertions.assertThat(costsReports).isNotNull();
        Assertions.assertThat(costsReports.size()).isEqualTo(instances.numClasses());
        Attribute classAttribute = instances.classAttribute();
        for (int i = 0; i < classAttribute.numValues(); i++) {
            Assertions.assertThat(costsReports.get(i).getClassValue()).isEqualTo(classAttribute.value(i));
            Assertions.assertThat(costsReports.get(i).getFalseNegativeRate().doubleValue()).isEqualTo(
                    evaluation.falseNegativeRate(i));
            Assertions.assertThat(costsReports.get(i).getFalsePositiveRate().doubleValue()).isEqualTo(
                    evaluation.falsePositiveRate(i));
            Assertions.assertThat(costsReports.get(i).getTrueNegativeRate().doubleValue()).isEqualTo(
                    evaluation.trueNegativeRate(i));
            Assertions.assertThat(costsReports.get(i).getTruePositiveRate().doubleValue()).isEqualTo(
                    evaluation.truePositiveRate(i));
            RocCurveReport rocCurveReport = costsReports.get(i).getRocCurve();
            Assertions.assertThat(rocCurveReport).isNotNull();
            Assertions.assertThat(rocCurveReport.getAucValue().doubleValue()).isEqualTo(evaluation.areaUnderROC(i));
            Assertions.assertThat(rocCurveReport.getSpecificity().doubleValue()).isNotNull();
            Assertions.assertThat(rocCurveReport.getSensitivity().doubleValue()).isNotNull();
            Assertions.assertThat(rocCurveReport.getThresholdValue().doubleValue()).isNotNull();
        }

        List<ConfusionMatrixReport> confusionMatrixReports = resultsRequest.getConfusionMatrix();
        Assertions.assertThat(confusionMatrixReports).isNotNull();
        double[][] confusionMatrix = evaluation.confusionMatrix();
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[i].length; j++) {
                ConfusionMatrixReport confusionMatrixReport =
                        confusionMatrixReports.get(i * confusionMatrix[i].length + j);
                Assertions.assertThat(confusionMatrixReport.getPredictedClass()).isEqualTo(classAttribute.value(j));
                Assertions.assertThat(confusionMatrixReport.getActualClass()).isEqualTo(classAttribute.value(i));
                Assertions.assertThat(confusionMatrixReport.getNumInstances().doubleValue()).isEqualTo(
                        confusionMatrix[i][j]);
            }
        }

        StatisticsReport statisticsReport = resultsRequest.getStatistics();
        Assertions.assertThat(statisticsReport).isNotNull();
        Assertions.assertThat(statisticsReport.getNumTestInstances().doubleValue()).isEqualTo(
                evaluation.numInstances());
        Assertions.assertThat(statisticsReport.getNumCorrect().doubleValue()).isEqualTo(evaluation.correct());
        Assertions.assertThat(statisticsReport.getNumIncorrect().doubleValue()).isEqualTo(evaluation.incorrect());
        Assertions.assertThat(statisticsReport.getPctCorrect().doubleValue()).isEqualTo(evaluation.pctCorrect());
        Assertions.assertThat(statisticsReport.getPctIncorrect().doubleValue()).isEqualTo(evaluation.pctIncorrect());
        Assertions.assertThat(statisticsReport.getMeanAbsoluteError().doubleValue()).isEqualTo(
                evaluation.meanAbsoluteError());
        Assertions.assertThat(statisticsReport.getRootMeanSquaredError().doubleValue()).isEqualTo(
                evaluation.rootMeanSquaredError());
        Assertions.assertThat(statisticsReport.getVarianceError().doubleValue()).isEqualTo(evaluation.varianceError());
        double[] confidenceInterval = evaluation.errorConfidenceInterval();
        Assertions.assertThat(statisticsReport.getConfidenceIntervalLowerBound().doubleValue()).isEqualTo(
                confidenceInterval[0]);
        Assertions.assertThat(statisticsReport.getConfidenceIntervalUpperBound().doubleValue()).isEqualTo(
                confidenceInterval[1]);
    }

    @Test
    public void testHeterogeneousClassifierMap() {
        HeterogeneousClassifier heterogeneousClassifier = new HeterogeneousClassifier();
        heterogeneousClassifier.setClassifiersSet(new ClassifiersSet());
        heterogeneousClassifier.getClassifiersSet().addClassifier(new CART());
        heterogeneousClassifier.getClassifiersSet().addClassifier(new Logistic());
        heterogeneousClassifier.getClassifiersSet().addClassifier(new KNearestNeighbours());
        EvaluationResults results = new EvaluationResults(heterogeneousClassifier, null);
        EvaluationResultsRequest resultsRequest = evaluationResultsMapper.map(results);
        Assertions.assertThat(resultsRequest.getClassifierReport()).isNotNull();
        Assertions.assertThat(resultsRequest.getClassifierReport()).isInstanceOf(EnsembleClassifierReport.class);
        EnsembleClassifierReport classifierReport = (EnsembleClassifierReport) resultsRequest.getClassifierReport();
        Assertions.assertThat(classifierReport.getIndividualClassifiers().size()).isEqualTo(
                heterogeneousClassifier.getClassifiersSet().size());
    }

    @Test
    public void testStackingClassifierMap() {
        StackingClassifier stackingClassifier = new StackingClassifier();
        stackingClassifier.setClassifiers(new ClassifiersSet());
        stackingClassifier.getClassifiers().addClassifier(new CART());
        stackingClassifier.getClassifiers().addClassifier(new Logistic());
        stackingClassifier.getClassifiers().addClassifier(new KNearestNeighbours());
        stackingClassifier.setMetaClassifier(new J48());
        EvaluationResults results = new EvaluationResults(stackingClassifier, null);
        EvaluationResultsRequest resultsRequest = evaluationResultsMapper.map(results);
        Assertions.assertThat(resultsRequest.getClassifierReport()).isNotNull();
        Assertions.assertThat(resultsRequest.getClassifierReport()).isInstanceOf(EnsembleClassifierReport.class);
        EnsembleClassifierReport classifierReport = (EnsembleClassifierReport) resultsRequest.getClassifierReport();
        Assertions.assertThat(classifierReport.getIndividualClassifiers().size()).isEqualTo(
                stackingClassifier.getClassifiers().size() + 1);
        ClassifierReport metaClassifierReport =
                classifierReport.getIndividualClassifiers().get(classifierReport.getIndividualClassifiers().size() - 1);
        Assertions.assertThat(metaClassifierReport.getClassifierDescription()).isNotNull();
    }
}
