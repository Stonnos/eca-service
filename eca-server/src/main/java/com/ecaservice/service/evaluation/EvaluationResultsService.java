package com.ecaservice.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.classifier.options.model.ClassifierOptions;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.dto.ClassifierInputOption;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.ConfusionMatrixReport;
import com.ecaservice.ers.dto.EnsembleClassifierReport;
import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.RocCurveReport;
import com.ecaservice.ers.dto.StatisticsReport;
import com.ecaservice.mapping.InstancesConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.EnsembleUtils;
import eca.ensemble.StackingClassifier;
import eca.roc.RocCurve;
import eca.roc.ThresholdModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Implements evaluation results service.
 *
 * @author Roman Batygin
 */
@Service
@RequiredArgsConstructor
public class EvaluationResultsService {

    private static final int CONFIDENCE_INTERVAL_LOWER_INDEX = 0;
    private static final int CONFIDENCE_INTERVAL_UPPER_INDEX = 1;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final CrossValidationConfig crossValidationConfig;
    private final InstancesConverter instancesConverter;

    /**
     * Gets evaluation results request model for ERS wev - service
     *
     * @param evaluationResults - evaluation results
     * @return evaluation results request model
     */
    public EvaluationResultsRequest proceed(EvaluationResults evaluationResults) {
        EvaluationResultsRequest evaluationResultsRequest = new EvaluationResultsRequest();
        evaluationResultsRequest.setInstances(instancesConverter.convert(evaluationResults.getEvaluation().getData()));
        populateEvaluationMethodReport(evaluationResults, evaluationResultsRequest);
        populateClassifierReport(evaluationResults, evaluationResultsRequest);
        populateStatisticsReport(evaluationResults, evaluationResultsRequest);
        populateConfusionMatrix(evaluationResults, evaluationResultsRequest);
        populateClassificationCostsReport(evaluationResults, evaluationResultsRequest);
        return evaluationResultsRequest;
    }

    private void populateEvaluationMethodReport(EvaluationResults evaluationResults,
                                                EvaluationResultsRequest evaluationResultsRequest) {
        EvaluationMethodReport evaluationMethodReport = new EvaluationMethodReport();
        Evaluation evaluation = evaluationResults.getEvaluation();
        evaluationMethodReport.setEvaluationMethod(
                evaluation.isKCrossValidationMethod() ? EvaluationMethod.CROSS_VALIDATION :
                        EvaluationMethod.TRAINING_DATA);
        if (evaluation.isKCrossValidationMethod()) {
            evaluationMethodReport.setNumFolds(BigInteger.valueOf(evaluation.numFolds()));
            evaluationMethodReport.setNumTests(BigInteger.valueOf(evaluation.getValidationsNum()));
            evaluationMethodReport.setSeed(BigInteger.valueOf(crossValidationConfig.getSeed()));
        }
        evaluationResultsRequest.setEvaluationMethodReport(evaluationMethodReport);
    }

    private void populateClassifierReport(EvaluationResults evaluationResults,
                                          EvaluationResultsRequest evaluationResultsRequest) {
        AbstractClassifier classifier = (AbstractClassifier) evaluationResults.getClassifier();
        evaluationResultsRequest.setClassifierReport(EnsembleUtils.isHeterogeneousEnsembleClassifier(classifier) ?
                buildEnsembleClassifierReport(classifier) : buildClassifierReport(classifier));
    }

    private void populateStatisticsReport(EvaluationResults evaluationResults,
                                          EvaluationResultsRequest evaluationResultsRequest) {
        StatisticsReport statisticsReport = new StatisticsReport();
        Evaluation evaluation = evaluationResults.getEvaluation();
        statisticsReport.setNumTestInstances(BigInteger.valueOf((long) evaluation.numInstances()));
        statisticsReport.setNumCorrect(BigInteger.valueOf((long) evaluation.correct()));
        statisticsReport.setNumIncorrect(BigInteger.valueOf((long) evaluation.incorrect()));
        statisticsReport.setPctCorrect(BigDecimal.valueOf(evaluation.pctCorrect()));
        statisticsReport.setPctIncorrect(BigDecimal.valueOf(evaluation.pctIncorrect()));
        statisticsReport.setMeanAbsoluteError(BigDecimal.valueOf(evaluation.meanAbsoluteError()));
        statisticsReport.setRootMeanSquaredError(BigDecimal.valueOf(evaluation.rootMeanSquaredError()));
        double maxAucValue = evaluation.maxAreaUnderROC();
        statisticsReport.setMaxAucValue(
                !Utils.isMissingValue(maxAucValue) ? BigDecimal.valueOf(maxAucValue) : null);
        if (evaluation.isKCrossValidationMethod()) {
            statisticsReport.setVarianceError(BigDecimal.valueOf(evaluation.varianceError()));
            double[] errorConfidenceInterval = evaluation.errorConfidenceInterval();
            statisticsReport.setConfidenceIntervalLowerBound(
                    BigDecimal.valueOf(errorConfidenceInterval[CONFIDENCE_INTERVAL_LOWER_INDEX]));
            statisticsReport.setConfidenceIntervalUpperBound(
                    BigDecimal.valueOf(errorConfidenceInterval[CONFIDENCE_INTERVAL_UPPER_INDEX]));
        }
        evaluationResultsRequest.setStatistics(statisticsReport);
    }

    private void populateConfusionMatrix(EvaluationResults evaluationResults,
                                         EvaluationResultsRequest evaluationResultsRequest) {
        double[][] confusionMatrix = evaluationResults.getEvaluation().confusionMatrix();
        Attribute classAttribute = evaluationResults.getEvaluation().getData().classAttribute();
        evaluationResultsRequest.setConfusionMatrix(newArrayList());
        for (int i = 0; i < confusionMatrix.length; i++) {
            for (int j = 0; j < confusionMatrix[i].length; j++) {
                ConfusionMatrixReport confusionMatrixReport = new ConfusionMatrixReport();
                confusionMatrixReport.setPredictedClass(classAttribute.value(j));
                confusionMatrixReport.setActualClass(classAttribute.value(i));
                confusionMatrixReport.setNumInstances(BigInteger.valueOf((long) confusionMatrix[i][j]));
                evaluationResultsRequest.getConfusionMatrix().add(confusionMatrixReport);
            }
        }
    }

    private void populateClassificationCostsReport(EvaluationResults evaluationResults,
                                                   EvaluationResultsRequest evaluationResultsRequest) {
        Evaluation evaluation = evaluationResults.getEvaluation();
        Attribute classAttribute = evaluationResults.getEvaluation().getData().classAttribute();
        RocCurve rocCurve = new RocCurve(evaluation);
        evaluationResultsRequest.setClassificationCosts(newArrayList());
        for (int i = 0; i < classAttribute.numValues(); i++) {
            ClassificationCostsReport classificationCostsReport = new ClassificationCostsReport();
            classificationCostsReport.setClassValue(classAttribute.value(i));
            classificationCostsReport.setFalseNegativeRate(BigDecimal.valueOf(evaluation.falseNegativeRate(i)));
            classificationCostsReport.setFalsePositiveRate(BigDecimal.valueOf(evaluation.falsePositiveRate(i)));
            classificationCostsReport.setTrueNegativeRate(BigDecimal.valueOf(evaluation.trueNegativeRate(i)));
            classificationCostsReport.setTruePositiveRate(BigDecimal.valueOf(evaluation.truePositiveRate(i)));
            classificationCostsReport.setRocCurve(populateRocCurveReport(rocCurve, i));
            evaluationResultsRequest.getClassificationCosts().add(classificationCostsReport);
        }
    }

    private RocCurveReport populateRocCurveReport(RocCurve rocCurve, int classIndex) {
        RocCurveReport rocCurveReport = new RocCurveReport();
        double aucValue = rocCurve.evaluation().areaUnderROC(classIndex);
        rocCurveReport.setAucValue(!Utils.isMissingValue(aucValue) ? BigDecimal.valueOf(aucValue) : null);
        Instances rocCurveData = rocCurve.getROCCurve(classIndex);
        ThresholdModel thresholdModel = rocCurve.findOptimalThreshold(rocCurveData);
        if (thresholdModel != null) {
            rocCurveReport.setSpecificity(BigDecimal.valueOf(1.0d - thresholdModel.getSpecificity()));
            rocCurveReport.setSensitivity(BigDecimal.valueOf(thresholdModel.getSensitivity()));
            rocCurveReport.setThresholdValue(BigDecimal.valueOf(thresholdModel.getThresholdValue()));
        }
        return rocCurveReport;
    }

    private ClassifierReport buildClassifierReport(AbstractClassifier classifier) {
        ClassifierReport classifierReport = new ClassifierReport();
        classifierReport.setClassifierName(classifier.getClass().getSimpleName());
        classifierReport.setOptions(getClassifierOptionsAsJsonString(classifier));
        classifierReport.setClassifierInputOptions(populateInputOptions(classifier));
        return classifierReport;
    }

    private EnsembleClassifierReport buildEnsembleClassifierReport(AbstractClassifier classifier) {
        EnsembleClassifierReport classifierReport = new EnsembleClassifierReport();
        classifierReport.setClassifierName(classifier.getClass().getSimpleName());
        classifierReport.setOptions(getClassifierOptionsAsJsonString(classifier));
        classifierReport.setClassifierInputOptions(populateInputOptions(classifier));
        populateIndividualClassifiers(classifierReport, classifier);
        return classifierReport;
    }

    private List<ClassifierInputOption> populateInputOptions(AbstractClassifier classifier) {
        List<ClassifierInputOption> options = newArrayList();
        String[] classifierOptions = classifier.getOptions();
        for (int i = 0; i < classifierOptions.length; i += 2) {
            options.add(new ClassifierInputOption(classifierOptions[i], classifierOptions[i + 1]));
        }
        return options;
    }

    private void populateIndividualClassifiers(EnsembleClassifierReport classifierReport,
                                               AbstractClassifier classifier) {
        classifierReport.setIndividualClassifiers(newArrayList());
        if (classifier instanceof AbstractHeterogeneousClassifier) {
            AbstractHeterogeneousClassifier heterogeneousClassifier =
                    (AbstractHeterogeneousClassifier) classifier;
            heterogeneousClassifier.getClassifiersSet().toList().forEach(c -> {
                ClassifierReport report = buildClassifierReport((AbstractClassifier) c);
                classifierReport.getIndividualClassifiers().add(report);
            });
        } else if (classifier instanceof StackingClassifier) {
            StackingClassifier stackingClassifier = (StackingClassifier) classifier;
            stackingClassifier.getClassifiers().toList().forEach(c -> {
                ClassifierReport report = buildClassifierReport((AbstractClassifier) c);
                classifierReport.getIndividualClassifiers().add(report);
            });
            ClassifierReport metaClassifierReport =
                    buildClassifierReport((AbstractClassifier) stackingClassifier.getMetaClassifier());
            metaClassifierReport.setMetaClassifier(true);
            classifierReport.getIndividualClassifiers().add(metaClassifierReport);
        } else {
            throw new IllegalStateException(
                    String.format("Unexpected ensemble classifier: %s!", classifier.getClass().getSimpleName()));
        }
    }

    private String getClassifierOptionsAsJsonString(AbstractClassifier classifier) {
        ClassifierOptions classifierOptions = classifierOptionsAdapter.convert(classifier);
        try {
            return objectMapper.writeValueAsString(classifierOptions);
        } catch (IOException ex) {
            throw new IllegalStateException(String.format("Can't serialize classifier [%s] options to json: %s",
                    classifier.getClass().getSimpleName(), ex.getMessage()));
        }
    }
}
