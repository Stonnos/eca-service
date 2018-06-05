package com.ecaservice.mapping;

import com.ecaservice.dto.evaluation.ClassificationCostsReport;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.dto.evaluation.ConfusionMatrixReport;
import com.ecaservice.dto.evaluation.EnsembleClassifierReport;
import com.ecaservice.dto.evaluation.EvaluationMethod;
import com.ecaservice.dto.evaluation.EvaluationMethodReport;
import com.ecaservice.dto.evaluation.EvaluationResultsRequest;
import com.ecaservice.dto.evaluation.InputOptionsMap;
import com.ecaservice.dto.evaluation.InstancesReport;
import com.ecaservice.dto.evaluation.RocCurveReport;
import com.ecaservice.dto.evaluation.StatisticsReport;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.service.ClassifierOptionsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.ensemble.AbstractHeterogeneousClassifier;
import eca.ensemble.EnsembleUtils;
import eca.ensemble.StackingClassifier;
import eca.roc.RocCurve;
import eca.roc.ThresholdModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.xml.XMLInstances;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Implements mapping evaluation results to evaluation results request model or web - service.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class EvaluationResultsMapper {

    private static final Logger log = LoggerFactory.getLogger(EvaluationResultsMapper.class);

    private static final int CONFIDENCE_INTERVAL_LOWER_INDEX = 0;
    private static final int CONFIDENCE_INTERVAL_UPPER_INDEX = 1;
    private static final String META_CLASSIFIER_DESCRIPTION = "Meta classifier";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    private ClassifierOptionsService classifierOptionsService;

    /**
     * Maps evaluation results to evaluation results request model.
     *
     * @param evaluationResults - evaluation results
     * @return evaluation results request model
     */
    public abstract EvaluationResultsRequest map(EvaluationResults evaluationResults);

    /**
     * Populates training data report.
     *
     * @param evaluationResults        - evaluation results
     * @param evaluationResultsRequest - evaluation results request model
     */
    @AfterMapping
    protected void populateInstancesReport(EvaluationResults evaluationResults,
                                           @MappingTarget EvaluationResultsRequest evaluationResultsRequest) {
        if (Optional.ofNullable(evaluationResults.getEvaluation()).map(Evaluation::getData).isPresent()) {
            Instances instances = evaluationResults.getEvaluation().getData();
            InstancesReport instancesReport = new InstancesReport();
            instancesReport.setRelationName(instances.relationName());
            instancesReport.setNumInstances(BigInteger.valueOf(instances.numInstances()));
            instancesReport.setNumAttributes(BigInteger.valueOf(instances.numAttributes()));
            instancesReport.setNumClasses(BigInteger.valueOf(instances.numClasses()));
            instancesReport.setClassName(instances.classAttribute().name());
            try {
                XMLInstances xmlInstances = new XMLInstances();
                xmlInstances.setInstances(instances);
                instancesReport.setXmlData(xmlInstances.toString());
            } catch (Exception ex) {
                log.error("Can't serialize instances [{}] to xml: {}", instances.relationName(), ex.getMessage());
            }
            evaluationResultsRequest.setInstances(instancesReport);
        }
    }

    /**
     * Populates evaluation method report.
     *
     * @param evaluationResults        - evaluation results
     * @param evaluationResultsRequest - evaluation results request model
     */
    @AfterMapping
    protected void populateEvaluationMethodReport(EvaluationResults evaluationResults,
                                                  @MappingTarget EvaluationResultsRequest evaluationResultsRequest) {
        if (evaluationResults.getEvaluation() != null) {
            EvaluationMethodReport evaluationMethodReport = new EvaluationMethodReport();
            Evaluation evaluation = evaluationResults.getEvaluation();
            evaluationMethodReport.setEvaluationMethod(
                    evaluation.isKCrossValidationMethod() ? EvaluationMethod.CROSS_VALIDATION :
                            EvaluationMethod.TRAINING_DATA);
            if (evaluation.isKCrossValidationMethod()) {
                evaluationMethodReport.setNumFolds(BigInteger.valueOf(evaluation.numFolds()));
                evaluationMethodReport.setNumTests(BigInteger.valueOf(evaluation.getValidationsNum()));
            }
            evaluationResultsRequest.setEvaluationMethodReport(evaluationMethodReport);
        }
    }

    /**
     * Populates classifier report.
     *
     * @param evaluationResults        - evaluation results
     * @param evaluationResultsRequest - evaluation results request model
     */
    @AfterMapping
    protected void populateClassifierReport(EvaluationResults evaluationResults,
                                            @MappingTarget EvaluationResultsRequest evaluationResultsRequest) {
        if (evaluationResults.getClassifier() != null) {
            AbstractClassifier classifier = (AbstractClassifier) evaluationResults.getClassifier();
            evaluationResultsRequest.setClassifierReport(EnsembleUtils.isHeterogeneousEnsembleClassifier(classifier) ?
                    buildEnsembleClassifierReport(classifier) : buildClassifierReport(classifier));
        }
    }

    /**
     * Populates statistics report.
     *
     * @param evaluationResults        - evaluation results
     * @param evaluationResultsRequest - evaluation results request model
     */
    @AfterMapping
    protected void populateStatisticsReport(EvaluationResults evaluationResults,
                                            @MappingTarget EvaluationResultsRequest evaluationResultsRequest) {
        if (evaluationResults.getEvaluation() != null) {
            StatisticsReport statisticsReport = new StatisticsReport();
            Evaluation evaluation = evaluationResults.getEvaluation();
            statisticsReport.setNumTestInstances(BigInteger.valueOf((long) evaluation.numInstances()));
            statisticsReport.setNumCorrect(BigInteger.valueOf((long) evaluation.correct()));
            statisticsReport.setNumIncorrect(BigInteger.valueOf((long) evaluation.incorrect()));
            statisticsReport.setPctCorrect(BigDecimal.valueOf(evaluation.pctCorrect()));
            statisticsReport.setPctIncorrect(BigDecimal.valueOf(evaluation.pctIncorrect()));
            statisticsReport.setMeanAbsoluteError(BigDecimal.valueOf(evaluation.meanAbsoluteError()));
            statisticsReport.setRootMeanSquaredError(BigDecimal.valueOf(evaluation.rootMeanSquaredError()));
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
    }

    /**
     * Populates confusion matrix report.
     *
     * @param evaluationResults        - evaluation results
     * @param evaluationResultsRequest - evaluation results request model
     */
    @AfterMapping
    protected void populateConfusionMatrix(EvaluationResults evaluationResults,
                                           @MappingTarget EvaluationResultsRequest evaluationResultsRequest) {
        if (Optional.ofNullable(evaluationResults.getEvaluation()).map(Evaluation::getData).isPresent()) {
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
    }

    /**
     * Populates classification costs report.
     *
     * @param evaluationResults        - evaluation results
     * @param evaluationResultsRequest - evaluation results request model
     */
    @AfterMapping
    protected void populateClassificationCostsReport(EvaluationResults evaluationResults,
                                                     @MappingTarget EvaluationResultsRequest evaluationResultsRequest) {
        if (Optional.ofNullable(evaluationResults.getEvaluation()).map(Evaluation::getData).isPresent()) {
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
    }

    private RocCurveReport populateRocCurveReport(RocCurve rocCurve, int classIndex) {
        RocCurveReport rocCurveReport = new RocCurveReport();
        rocCurveReport.setAucValue(BigDecimal.valueOf(rocCurve.evaluation().areaUnderROC(classIndex)));
        ThresholdModel thresholdModel = rocCurve.findOptimalThreshold(classIndex);
        if (thresholdModel != null) {
            rocCurveReport.setSpecificity(BigDecimal.valueOf(1.0 - thresholdModel.getSpecificity()));
            rocCurveReport.setSensitivity(BigDecimal.valueOf(thresholdModel.getSensitivity()));
            rocCurveReport.setThresholdValue(BigDecimal.valueOf(thresholdModel.getThresholdValue()));
        }
        return rocCurveReport;
    }

    private ClassifierReport buildClassifierReport(AbstractClassifier classifier) {
        ClassifierReport classifierReport = new ClassifierReport();
        classifierReport.setClassifierName(classifier.getClass().getSimpleName());
        classifierReport.setConfig(getClassifierOptionsAsJsonString(classifier));
        classifierReport.setInputOptionsMap(populateInputOptionsMap(classifier));
        return classifierReport;
    }

    private EnsembleClassifierReport buildEnsembleClassifierReport(AbstractClassifier classifier) {
        EnsembleClassifierReport classifierReport = new EnsembleClassifierReport();
        classifierReport.setClassifierName(classifier.getClass().getSimpleName());
        classifierReport.setConfig(getClassifierOptionsAsJsonString(classifier));
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
        ClassifierOptions classifierOptions = classifierOptionsService.convert(classifier);
        try {
            return objectMapper.writeValueAsString(classifierOptions);
        } catch (IOException ex) {
            log.error("Can't serialize classifier [{}] options to json: {}", classifier.getClass().getSimpleName(),
                    ex.getMessage());
        }
        return null;
    }
}
