package com.ecaservice.service.evaluation;

import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.conversion.ClassifierOptionsConverter;
import com.ecaservice.dto.evaluation.ClassificationCostsReport;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.dto.evaluation.ConfusionMatrixReport;
import com.ecaservice.dto.evaluation.EnsembleClassifierReport;
import com.ecaservice.dto.evaluation.EvaluationMethod;
import com.ecaservice.dto.evaluation.EvaluationMethodReport;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.InputOptionsMap;
import com.ecaservice.dto.evaluation.RocCurveReport;
import com.ecaservice.dto.evaluation.StatisticsReport;
import com.ecaservice.mapping.InstancesConverter;
import com.ecaservice.model.options.ClassifierOptions;
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
    private static final String META_CLASSIFIER_DESCRIPTION = "Meta classifier";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ClassifierOptionsConverter classifierOptionsConverter;
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
        classifierReport.setInputOptionsMap(populateInputOptionsMap(classifier));
        return classifierReport;
    }

    private EnsembleClassifierReport buildEnsembleClassifierReport(AbstractClassifier classifier) {
        EnsembleClassifierReport classifierReport = new EnsembleClassifierReport();
        classifierReport.setClassifierName(classifier.getClass().getSimpleName());
        classifierReport.setOptions(getClassifierOptionsAsJsonString(classifier));
        classifierReport.setInputOptionsMap(populateInputOptionsMap(classifier));
        populateIndividualClassifiers(classifierReport.getIndividualClassifiers(), classifier);
        return classifierReport;
    }

    private InputOptionsMap populateInputOptionsMap(AbstractClassifier classifier) {
        InputOptionsMap inputOptionsMap = new InputOptionsMap();
        String[] classifierOptions = classifier.getOptions();
        for (int i = 0; i < classifierOptions.length; i += 2) {
            InputOptionsMap.Entry entry = new InputOptionsMap.Entry();
            entry.setKey(classifierOptions[i]);
            entry.setValue(classifierOptions[i + 1]);
            inputOptionsMap.getEntry().add(entry);
        }
        return inputOptionsMap;
    }

    private void populateIndividualClassifiers(List<ClassifierReport> classifierReportList,
                                               AbstractClassifier classifier) {
        if (classifier instanceof AbstractHeterogeneousClassifier) {
            AbstractHeterogeneousClassifier heterogeneousClassifier =
                    (AbstractHeterogeneousClassifier) classifier;
            heterogeneousClassifier.getClassifiersSet().toList().forEach(c -> classifierReportList.add(
                    buildClassifierReport((AbstractClassifier) c)));
        } else if (classifier instanceof StackingClassifier) {
            StackingClassifier stackingClassifier = (StackingClassifier) classifier;
            stackingClassifier.getClassifiers().toList().forEach(
                    c -> classifierReportList.add(buildClassifierReport((AbstractClassifier) c)));
            ClassifierReport metaClassifierReport =
                    buildClassifierReport((AbstractClassifier) stackingClassifier.getMetaClassifier());
            metaClassifierReport.setClassifierDescription(META_CLASSIFIER_DESCRIPTION);
            classifierReportList.add(metaClassifierReport);
        } else {
            throw new IllegalArgumentException(
                    String.format("Unexpected ensemble classifier: %s!", classifier.getClass().getSimpleName()));
        }
    }

    private String getClassifierOptionsAsJsonString(AbstractClassifier classifier) {
        ClassifierOptions classifierOptions = classifierOptionsConverter.convert(classifier);
        try {
            return objectMapper.writeValueAsString(classifierOptions);
        } catch (IOException ex) {
            throw new IllegalStateException(String.format("Can't serialize classifier [%s] options to json: %s",
                    classifier.getClass().getSimpleName(), ex.getMessage()));
        }
    }
}
