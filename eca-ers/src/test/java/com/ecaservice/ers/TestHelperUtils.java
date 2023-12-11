package com.ecaservice.ers;

import com.ecaservice.ers.dto.ClassificationCostsReport;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.ConfusionMatrixReport;
import com.ecaservice.ers.dto.EvaluationMethod;
import com.ecaservice.ers.dto.EvaluationMethodReport;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.ers.dto.RocCurveReport;
import com.ecaservice.ers.dto.SortDirection;
import com.ecaservice.ers.dto.EvaluationResultsStatisticsField;
import com.ecaservice.ers.dto.StatisticsReport;
import com.ecaservice.ers.dto.EvaluationResultsStatisticsSortField;
import com.ecaservice.ers.model.ClassificationCostsInfo;
import com.ecaservice.ers.model.ClassifierOptionsInfo;
import com.ecaservice.ers.model.ConfusionMatrix;
import com.ecaservice.ers.model.EvaluationResultsInfo;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.ers.model.RocCurveInfo;
import com.ecaservice.ers.model.StatisticsInfo;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Tests utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final int OPTIONS_SIZE = 5;
    private static final int NUM_FOLDS = 10;
    private static final int NUM_TESTS = 1;
    private static final int SEED = 1;
    private static final String RELATION_NAME = "relation";
    private static final String CLASS_NAME = "class";
    private static final String ACTUAL_CLASS = "actual";
    private static final String PREDICTED_CLASS = "predicted";
    private static final String CLASSIFIER_NAME = "Classifier";
    private static final String CLASSIFIER_DESCRIPTION = "description";
    private static final String OPTIONS = "options";
    private static final String DATA_HASH = "3032e188204cb537f69fc7364f638641";

    /**
     * Creates evaluation results report.
     *
     * @param requestId - request id
     * @return evaluation results report
     */
    public static EvaluationResultsRequest buildEvaluationResultsReport(String requestId) {
        EvaluationResultsRequest resultsRequest = new EvaluationResultsRequest();
        resultsRequest.setRequestId(requestId);
        resultsRequest.setInstances(buildInstancesReport());
        resultsRequest.setEvaluationMethodReport(buildEvaluationMethodReport(EvaluationMethod.CROSS_VALIDATION));
        resultsRequest.setClassifierReport(buildClassifierReport());
        resultsRequest.setStatistics(buildStatisticsReport());
        resultsRequest.setConfusionMatrix(newArrayList());
        resultsRequest.setClassificationCosts(newArrayList());
        for (int i = 0; i < OPTIONS_SIZE; i++) {
            resultsRequest.getConfusionMatrix().add(buildConfusionMatrixReport());
            resultsRequest.getClassificationCosts().add(buildClassificationCostsReport());
        }
        return resultsRequest;
    }

    /**
     * Creates instances report.
     *
     * @return instances report
     */
    public static InstancesReport buildInstancesReport() {
        InstancesReport instancesReport = new InstancesReport();
        instancesReport.setDataMd5Hash(DATA_HASH);
        instancesReport.setRelationName(RELATION_NAME);
        instancesReport.setNumInstances(BigInteger.TEN);
        instancesReport.setNumAttributes(BigInteger.TEN);
        instancesReport.setNumClasses(BigInteger.TEN);
        instancesReport.setClassName(CLASS_NAME);
        return instancesReport;
    }

    /**
     * Creates instances info.
     *
     * @return instances info
     */
    public static InstancesInfo buildInstancesInfo() {
        InstancesInfo instancesInfo = new InstancesInfo();
        instancesInfo.setDataMd5Hash(DATA_HASH);
        instancesInfo.setRelationName(RELATION_NAME);
        instancesInfo.setNumInstances(BigInteger.TEN.intValue());
        instancesInfo.setNumAttributes(BigInteger.TEN.intValue());
        instancesInfo.setNumClasses(BigInteger.TEN.intValue());
        instancesInfo.setClassName(CLASS_NAME);
        instancesInfo.setCreatedDate(LocalDateTime.now());
        return instancesInfo;
    }

    /**
     * Creates confusion matrix report.
     *
     * @return confusion matrix report
     */
    public static ConfusionMatrixReport buildConfusionMatrixReport() {
        ConfusionMatrixReport confusionMatrix = new ConfusionMatrixReport();
        confusionMatrix.setActualClass(ACTUAL_CLASS);
        confusionMatrix.setPredictedClass(PREDICTED_CLASS);
        confusionMatrix.setNumInstances(BigInteger.TEN);
        return confusionMatrix;
    }

    /**
     * Creates confusion matrix.
     *
     * @return confusion matrix
     */
    public static ConfusionMatrix buildConfusionMatrix() {
        ConfusionMatrix confusionMatrix = new ConfusionMatrix();
        confusionMatrix.setActualClass(ACTUAL_CLASS);
        confusionMatrix.setPredictedClass(PREDICTED_CLASS);
        confusionMatrix.setNumInstances(BigInteger.TEN.intValue());
        return confusionMatrix;
    }

    /**
     * Creates classification costs report.
     *
     * @return classification costs report
     */
    public static ClassificationCostsReport buildClassificationCostsReport() {
        ClassificationCostsReport classificationCostsReport = new ClassificationCostsReport();
        classificationCostsReport.setClassValue(CLASS_NAME);
        classificationCostsReport.setFalseNegativeRate(BigDecimal.valueOf(Math.random()));
        classificationCostsReport.setTrueNegativeRate(BigDecimal.valueOf(Math.random()));
        classificationCostsReport.setTruePositiveRate(BigDecimal.valueOf(Math.random()));
        classificationCostsReport.setFalsePositiveRate(BigDecimal.valueOf(Math.random()));
        classificationCostsReport.setRocCurve(buildRocCurveReport());
        return classificationCostsReport;
    }

    /**
     * Creates classification costs info.
     *
     * @return classification costs info
     */
    public static ClassificationCostsInfo buildClassificationCostsInfo() {
        ClassificationCostsInfo classificationCostsInfo = new ClassificationCostsInfo();
        classificationCostsInfo.setClassValue(CLASS_NAME);
        classificationCostsInfo.setFalseNegativeRate(BigDecimal.valueOf(Math.random()));
        classificationCostsInfo.setTrueNegativeRate(BigDecimal.valueOf(Math.random()));
        classificationCostsInfo.setTruePositiveRate(BigDecimal.valueOf(Math.random()));
        classificationCostsInfo.setFalsePositiveRate(BigDecimal.valueOf(Math.random()));
        classificationCostsInfo.setRocCurveInfo(buildRocCurveInfo());
        return classificationCostsInfo;
    }

    /**
     * Creates roc - curve report.
     *
     * @return roc - curve report
     */
    public static RocCurveReport buildRocCurveReport() {
        RocCurveReport rocCurveReport = new RocCurveReport();
        rocCurveReport.setAucValue(BigDecimal.valueOf(Math.random()));
        rocCurveReport.setSpecificity(BigDecimal.valueOf(Math.random()));
        rocCurveReport.setSensitivity(BigDecimal.valueOf(Math.random()));
        rocCurveReport.setThresholdValue(BigDecimal.valueOf(Math.random()));
        return rocCurveReport;
    }

    /**
     * Creates roc - curve info.
     *
     * @return roc - curve info
     */
    public static RocCurveInfo buildRocCurveInfo() {
        RocCurveInfo rocCurveInfo = new RocCurveInfo();
        rocCurveInfo.setAucValue(BigDecimal.valueOf(Math.random()));
        rocCurveInfo.setSpecificity(BigDecimal.valueOf(Math.random()));
        rocCurveInfo.setSensitivity(BigDecimal.valueOf(Math.random()));
        rocCurveInfo.setThresholdValue(BigDecimal.valueOf(Math.random()));
        return rocCurveInfo;
    }

    /**
     * Creates statistics report.
     *
     * @return statistics report
     */
    public static StatisticsReport buildStatisticsReport() {
        StatisticsReport statisticsReport = new StatisticsReport();
        statisticsReport.setPctCorrect(BigDecimal.valueOf(Math.random()));
        statisticsReport.setPctIncorrect(BigDecimal.valueOf(Math.random()));
        statisticsReport.setNumTestInstances(BigInteger.TEN);
        statisticsReport.setNumCorrect(BigInteger.ZERO);
        statisticsReport.setNumIncorrect(BigInteger.TEN);
        statisticsReport.setMeanAbsoluteError(BigDecimal.valueOf(Math.random()));
        statisticsReport.setRootMeanSquaredError(BigDecimal.valueOf(Math.random()));
        statisticsReport.setMaxAucValue(BigDecimal.valueOf(Math.random()));
        statisticsReport.setVarianceError(BigDecimal.valueOf(Math.random()));
        statisticsReport.setConfidenceIntervalLowerBound(BigDecimal.valueOf(Math.random()));
        statisticsReport.setConfidenceIntervalUpperBound(BigDecimal.valueOf(Math.random()));
        return statisticsReport;
    }

    /**
     * Creates statistics info.
     *
     * @return statistics info
     */
    public static StatisticsInfo buildStatisticsInfo() {
        StatisticsInfo statisticsInfo = new StatisticsInfo();
        statisticsInfo.setPctCorrect(BigDecimal.valueOf(Math.random()));
        statisticsInfo.setPctIncorrect(BigDecimal.valueOf(Math.random()));
        statisticsInfo.setNumTestInstances(BigInteger.TEN.intValue());
        statisticsInfo.setNumCorrect(BigInteger.ZERO.intValue());
        statisticsInfo.setNumIncorrect(BigInteger.TEN.intValue());
        statisticsInfo.setMeanAbsoluteError(BigDecimal.valueOf(Math.random()));
        statisticsInfo.setRootMeanSquaredError(BigDecimal.valueOf(Math.random()));
        statisticsInfo.setMaxAucValue(BigDecimal.valueOf(Math.random()));
        statisticsInfo.setVarianceError(BigDecimal.valueOf(Math.random()));
        statisticsInfo.setConfidenceIntervalLowerBound(BigDecimal.valueOf(Math.random()));
        statisticsInfo.setConfidenceIntervalUpperBound(BigDecimal.valueOf(Math.random()));
        return statisticsInfo;
    }

    /**
     * Creates evaluation method report.
     *
     * @param evaluationMethod - evaluation method
     * @return evaluation method report
     */
    public static EvaluationMethodReport buildEvaluationMethodReport(EvaluationMethod evaluationMethod) {
        EvaluationMethodReport evaluationMethodReport = new EvaluationMethodReport();
        evaluationMethodReport.setEvaluationMethod(evaluationMethod);
        evaluationMethodReport.setNumFolds(BigInteger.valueOf(NUM_FOLDS));
        evaluationMethodReport.setNumTests(BigInteger.valueOf(NUM_TESTS));
        evaluationMethodReport.setSeed(BigInteger.valueOf(SEED));
        return evaluationMethodReport;
    }

    /**
     * Creates classifier report.
     *
     * @return classifier report
     */
    public static ClassifierReport buildClassifierReport() {
        ClassifierReport classifierReport = new ClassifierReport();
        classifierReport.setClassifierName(CLASSIFIER_NAME);
        classifierReport.setClassifierDescription(CLASSIFIER_DESCRIPTION);
        classifierReport.setOptions(OPTIONS);
        return classifierReport;
    }

    /**
     * Creates classifier options info list.
     *
     * @return classifier options info list
     */
    public static ClassifierOptionsInfo buildClassifierOptionsInfo() {
        ClassifierOptionsInfo classifierOptionsInfo = new ClassifierOptionsInfo();
        classifierOptionsInfo.setClassifierName(CLASSIFIER_NAME);
        classifierOptionsInfo.setClassifierDescription(CLASSIFIER_DESCRIPTION);
        classifierOptionsInfo.setOptions(OPTIONS);
        return classifierOptionsInfo;
    }

    /**
     * Creates classifier options request.
     *
     * @param evaluationMethod - evaluation method
     * @return classifier options request
     */
    public static ClassifierOptionsRequest createClassifierOptionsRequest(EvaluationMethod evaluationMethod) {
        ClassifierOptionsRequest request = new ClassifierOptionsRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setDataHash(DATA_HASH);
        request.setEvaluationMethodReport(buildEvaluationMethodReport(evaluationMethod));
        request.setEvaluationResultsStatisticsSortFields(newArrayList());
        request.getEvaluationResultsStatisticsSortFields().add(createSortField(EvaluationResultsStatisticsField.PCT_CORRECT, SortDirection.DESC));
        request.getEvaluationResultsStatisticsSortFields().add(createSortField(EvaluationResultsStatisticsField.MAX_AUC_VALUE, SortDirection.DESC));
        request.getEvaluationResultsStatisticsSortFields().add(createSortField(EvaluationResultsStatisticsField.VARIANCE_ERROR, SortDirection.ASC));
        return request;
    }

    /**
     * Creates sort field.
     *
     * @param fieldName - field name
     * @param direction - sort direction
     * @return sort field object
     */
    public static EvaluationResultsStatisticsSortField createSortField(EvaluationResultsStatisticsField fieldName, SortDirection direction) {
        EvaluationResultsStatisticsSortField sortField = new EvaluationResultsStatisticsSortField();
        sortField.setField(fieldName);
        sortField.setDirection(direction);
        return sortField;
    }

    /**
     * Creates evaluation results info.
     *
     * @param instancesInfo         - instances info
     * @param classifierOptionsInfo - classifier options info
     * @param evaluationMethod      - evaluation method
     * @param pctCorrect            - pct correct
     * @param maxAucValue           - max AUC value
     * @param varianceError         - variance error
     * @return evaluation results info
     */
    public static EvaluationResultsInfo createEvaluationResultsInfo(InstancesInfo instancesInfo,
                                                                    ClassifierOptionsInfo classifierOptionsInfo,
                                                                    EvaluationMethod evaluationMethod,
                                                                    BigDecimal pctCorrect,
                                                                    BigDecimal maxAucValue,
                                                                    BigDecimal varianceError) {
        EvaluationResultsInfo evaluationResultsInfo = new EvaluationResultsInfo();
        evaluationResultsInfo.setRequestId(UUID.randomUUID().toString());
        evaluationResultsInfo.setSaveDate(LocalDateTime.now());
        evaluationResultsInfo.setInstancesInfo(instancesInfo);
        evaluationResultsInfo.setClassifierInfo(classifierOptionsInfo);
        evaluationResultsInfo.setNumFolds(NUM_FOLDS);
        evaluationResultsInfo.setNumTests(NUM_TESTS);
        evaluationResultsInfo.setSeed(SEED);
        evaluationResultsInfo.setEvaluationMethod(evaluationMethod);
        evaluationResultsInfo.setStatistics(new StatisticsInfo());
        evaluationResultsInfo.getStatistics().setPctCorrect(pctCorrect);
        evaluationResultsInfo.getStatistics().setMaxAucValue(maxAucValue);
        evaluationResultsInfo.getStatistics().setVarianceError(varianceError);
        return evaluationResultsInfo;
    }

    /**
     * Creates evaluation results info.
     *
     * @return evaluation results info
     */
    public static EvaluationResultsInfo createEvaluationResultsInfo() {
        EvaluationResultsInfo evaluationResultsInfo = new EvaluationResultsInfo();
        evaluationResultsInfo.setInstancesInfo(buildInstancesInfo());
        evaluationResultsInfo.setClassifierInfo(buildClassifierOptionsInfo());
        evaluationResultsInfo.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        evaluationResultsInfo.setNumFolds(NUM_FOLDS);
        evaluationResultsInfo.setNumTests(NUM_TESTS);
        evaluationResultsInfo.setSeed(SEED);
        evaluationResultsInfo.setStatistics(new StatisticsInfo());
        evaluationResultsInfo.getStatistics().setPctCorrect(BigDecimal.TEN);
        evaluationResultsInfo.getStatistics().setMaxAucValue(BigDecimal.ONE);
        evaluationResultsInfo.getStatistics().setVarianceError(BigDecimal.ZERO);
        evaluationResultsInfo.getStatistics().setMeanAbsoluteError(BigDecimal.ZERO);
        evaluationResultsInfo.getStatistics().setRootMeanSquaredError(BigDecimal.ONE);
        evaluationResultsInfo.setRequestId(UUID.randomUUID().toString());
        evaluationResultsInfo.setSaveDate(LocalDateTime.now());
        return evaluationResultsInfo;
    }

    /**
     * Creates evaluation results get request.
     *
     * @param requestId - request id
     * @return evaluation results get request
     */
    public static GetEvaluationResultsRequest buildGetEvaluationResultsRequest(String requestId) {
        GetEvaluationResultsRequest request = new GetEvaluationResultsRequest();
        request.setRequestId(requestId);
        return request;
    }

    /**
     * Creates evaluation results response.
     *
     * @param requestId - request id
     * @return evaluation results response
     */
    public static EvaluationResultsResponse buildEvaluationResultsResponse(String requestId) {
        return EvaluationResultsResponse.builder()
                .requestId(requestId)
                .build();
    }

    /**
     * Creates evaluation results get response.
     *
     * @param requestId - request id
     * @return evaluation results get response
     */
    public static GetEvaluationResultsResponse buildGetEvaluationResultsResponse(String requestId) {
        GetEvaluationResultsResponse getEvaluationResultsResponse = new GetEvaluationResultsResponse();
        getEvaluationResultsResponse.setRequestId(requestId);
        getEvaluationResultsResponse.setClassifierReport(buildClassifierReport());
        getEvaluationResultsResponse.setEvaluationMethodReport(
                buildEvaluationMethodReport(EvaluationMethod.CROSS_VALIDATION));
        getEvaluationResultsResponse.setStatistics(buildStatisticsReport());
        getEvaluationResultsResponse.setInstances(buildInstancesReport());
        return getEvaluationResultsResponse;
    }
}
