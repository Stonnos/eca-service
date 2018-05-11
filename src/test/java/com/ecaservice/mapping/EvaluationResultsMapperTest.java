package com.ecaservice.mapping;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.evaluation.*;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.trees.CART;
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
import java.util.Random;

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
        CART cart = new CART();
        Instances testInstances = TestHelperUtils.loadInstances();
        cart.buildClassifier(testInstances);
        Evaluation evaluation = new Evaluation(testInstances);
        evaluation.kCrossValidateModel(cart, testInstances, TestHelperUtils.NUM_FOLDS, TestHelperUtils.NUM_TESTS, new Random());
        evaluationResults = new EvaluationResults(cart, evaluation);
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
        Assertions.assertThat(classifierReport.getInputOptionsMap().getEntry().size()).isEqualTo(classifier.getOptions().length / 2);

        EvaluationMethodReport evaluationMethodReport = resultsRequest.getEvaluationMethodReport();
        Assertions.assertThat(evaluationMethodReport).isNotNull();
        Evaluation evaluation = evaluationResults.getEvaluation();
        Assertions.assertThat(evaluationMethodReport.getEvaluationMethod()).isEqualTo(EvaluationMethod.CROSS_VALIDATION);
        Assertions.assertThat(evaluationMethodReport.getNumFolds().intValue()).isEqualTo(evaluation.numFolds());
        Assertions.assertThat(evaluationMethodReport.getNumTests().intValue()).isEqualTo(evaluation.getValidationsNum());

        List<ClassificationCostsReport> costsReports = resultsRequest.getClassificationCosts();
        Assertions.assertThat(costsReports).isNotNull();
        Assertions.assertThat(costsReports.size()).isEqualTo(instances.numClasses());
        Attribute classAttribute = instances.classAttribute();
        for (int i = 0; i < classAttribute.numValues(); i++) {
            Assertions.assertThat(costsReports.get(i).getClassValue()).isEqualTo(classAttribute.value(i));
            Assertions.assertThat(costsReports.get(i).getAucValue().doubleValue()).isEqualTo(evaluation.areaUnderROC(i));
            Assertions.assertThat(costsReports.get(i).getFalseNegativeRate().doubleValue()).isEqualTo(evaluation.falseNegativeRate(i));
            Assertions.assertThat(costsReports.get(i).getFalsePositiveRate().doubleValue()).isEqualTo(evaluation.falsePositiveRate(i));
            Assertions.assertThat(costsReports.get(i).getTrueNegativeRate().doubleValue()).isEqualTo(evaluation.trueNegativeRate(i));
            Assertions.assertThat(costsReports.get(i).getTruePositiveRate().doubleValue()).isEqualTo(evaluation.truePositiveRate(i));
        }

        List<ConfusionMatrixReport> confusionMatrixReports = resultsRequest.getConfusionMatrix();
        Assertions.assertThat(confusionMatrixReports).isNotNull();
        double[][] confusionMatrix = evaluation.confusionMatrix();
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[i].length; j++) {
                ConfusionMatrixReport confusionMatrixReport = confusionMatrixReports.get(i * confusionMatrix[i].length + j);
                Assertions.assertThat(confusionMatrixReport.getPredictedClass()).isEqualTo(classAttribute.value(j));
                Assertions.assertThat(confusionMatrixReport.getActualClass()).isEqualTo(classAttribute.value(i));
                Assertions.assertThat(confusionMatrixReport.getNumInstances().doubleValue()).isEqualTo(confusionMatrix[i][j]);
            }
        }
    }
}
