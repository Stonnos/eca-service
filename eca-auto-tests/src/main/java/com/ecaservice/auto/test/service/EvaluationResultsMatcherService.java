package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.model.evaluation.ConfusionMatrixDetailsMatch;
import com.ecaservice.auto.test.model.evaluation.EvaluationResultsDetailsMatch;
import com.ecaservice.ers.dto.ConfusionMatrixReport;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.dto.StatisticsReport;
import com.ecaservice.test.common.model.MatchResult;
import com.ecaservice.test.common.service.TestResultsMatcher;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import weka.core.Attribute;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.ecaservice.auto.test.util.Utils.getScaledValue;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Evaluation results matcher service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationResultsMatcherService {

    private static final int SCALE = 4;
    private static final int CONFIDENCE_INTERVAL_LOWER_INDEX = 0;
    private static final int CONFIDENCE_INTERVAL_UPPER_INDEX = 1;
    private static final String CONFUSION_MATRIX_ERROR_MESSAGE_FORMAT =
            "Invalid confusion matrix for ERS evaluation results [%s]. Expected only one confusion matrix item " +
                    "with actual class value [%s], predicted class value [%s]";
    private static final String INVALID_CONFUSION_MATRIX_SIZE_FORMAT =
            "Invalid confusion matrix size for ERS evaluation results [%s]. Expected [%d] items, actual [%d] items";

    /**
     * Compares and matches evaluation results.
     *
     * @param evaluationResults         - evaluation results
     * @param evaluationResultsResponse - evaluation results response from ERS
     * @param matcher                   - matcher object
     */
    public EvaluationResultsDetailsMatch compareAndMatch(EvaluationResults evaluationResults,
                                                         GetEvaluationResultsResponse evaluationResultsResponse,
                                                         TestResultsMatcher matcher) {
        log.info("Starting to compare and match ERS evaluation results with request id [{}]",
                evaluationResultsResponse.getRequestId());
        var evaluationResultsDetailsMatch = new EvaluationResultsDetailsMatch();
        var evaluation = evaluationResults.getEvaluation();
        compareAndMatchCommonStatistics(evaluation, evaluationResultsResponse.getStatistics(), matcher,
                evaluationResultsDetailsMatch);
        compareAndMatchConfusionMatrix(evaluation, evaluationResultsResponse, matcher, evaluationResultsDetailsMatch);
        log.info("ERS evaluation results [{}] comparison has been finished", evaluationResultsResponse.getRequestId());
        return evaluationResultsDetailsMatch;
    }

    private void compareAndMatchCommonStatistics(Evaluation evaluation,
                                                 StatisticsReport statisticsReport,
                                                 TestResultsMatcher matcher,
                                                 EvaluationResultsDetailsMatch evaluationResultsDetailsMatch) {
        BigInteger expectedNumTestInstances = BigInteger.valueOf((long) evaluation.numInstances());
        BigInteger actualNumTestInstances = statisticsReport.getNumTestInstances();
        MatchResult numTestInstancesMatchResult =
                matcher.compareAndMatch(expectedNumTestInstances, actualNumTestInstances);

        BigInteger expectedNumCorrect = BigInteger.valueOf((long) evaluation.correct());
        BigInteger actualNumCorrect = statisticsReport.getNumCorrect();
        MatchResult numCorrectMatchResult = matcher.compareAndMatch(expectedNumCorrect, actualNumCorrect);

        BigInteger expectedNumIncorrect = BigInteger.valueOf((long) evaluation.incorrect());
        BigInteger actualNumIncorrect = statisticsReport.getNumIncorrect();
        MatchResult numIncorrectMatchResult = matcher.compareAndMatch(expectedNumIncorrect, actualNumIncorrect);

        BigDecimal expectedPctCorrect = getScaledValue(evaluation.pctCorrect(), SCALE);
        BigDecimal actualPctCorrect = getScaledValue(statisticsReport.getPctCorrect(), SCALE);
        MatchResult pctCorrectMatchResult = matcher.compareAndMatch(expectedPctCorrect, actualPctCorrect);

        BigDecimal expectedPctIncorrect = getScaledValue(evaluation.pctIncorrect(), SCALE);
        BigDecimal actualPctIncorrect = getScaledValue(statisticsReport.getPctIncorrect(), SCALE);
        MatchResult pctIncorrectMatchResult = matcher.compareAndMatch(expectedPctIncorrect, actualPctIncorrect);

        BigDecimal expectedMeanAbsoluteError = getScaledValue(evaluation.meanAbsoluteError(), SCALE);
        BigDecimal actualMeanAbsoluteError = getScaledValue(statisticsReport.getMeanAbsoluteError(), SCALE);
        MatchResult meanAbsoluteErrorMatchResult =
                matcher.compareAndMatch(expectedMeanAbsoluteError, actualMeanAbsoluteError);

        BigDecimal expectedRootMeanSquaredError = getScaledValue(evaluation.rootMeanSquaredError(), SCALE);
        BigDecimal actualRootMeanSquaredError = getScaledValue(statisticsReport.getRootMeanSquaredError(), SCALE);
        MatchResult rootMeanSquaredErrorMatchResult =
                matcher.compareAndMatch(expectedRootMeanSquaredError, actualRootMeanSquaredError);

        BigDecimal expectedMaxAucValue = getScaledValue(evaluation.maxAreaUnderROC(), SCALE);
        BigDecimal actualMaxAucValue = getScaledValue(statisticsReport.getMaxAucValue(), SCALE);
        MatchResult maxAucValueMatchResult = matcher.compareAndMatch(expectedMaxAucValue, actualMaxAucValue);

        evaluationResultsDetailsMatch.setExpectedNumTestInstances(expectedNumTestInstances);
        evaluationResultsDetailsMatch.setActualNumTestInstances(actualNumTestInstances);
        evaluationResultsDetailsMatch.setNumTestInstancesMatchResult(numTestInstancesMatchResult);
        evaluationResultsDetailsMatch.setExpectedNumCorrect(expectedNumCorrect);
        evaluationResultsDetailsMatch.setActualNumCorrect(actualNumCorrect);
        evaluationResultsDetailsMatch.setNumCorrectMatchResult(numCorrectMatchResult);
        evaluationResultsDetailsMatch.setExpectedNumIncorrect(expectedNumIncorrect);
        evaluationResultsDetailsMatch.setActualNumIncorrect(actualNumIncorrect);
        evaluationResultsDetailsMatch.setNumIncorrectMatchResult(numIncorrectMatchResult);
        evaluationResultsDetailsMatch.setExpectedPctCorrect(expectedPctCorrect);
        evaluationResultsDetailsMatch.setActualPctCorrect(actualPctCorrect);
        evaluationResultsDetailsMatch.setPctCorrectMatchResult(pctCorrectMatchResult);
        evaluationResultsDetailsMatch.setExpectedPctIncorrect(expectedPctIncorrect);
        evaluationResultsDetailsMatch.setActualPctIncorrect(actualPctIncorrect);
        evaluationResultsDetailsMatch.setPctIncorrectMatchResult(pctIncorrectMatchResult);
        evaluationResultsDetailsMatch.setExpectedMeanAbsoluteError(expectedMeanAbsoluteError);
        evaluationResultsDetailsMatch.setActualMeanAbsoluteError(actualMeanAbsoluteError);
        evaluationResultsDetailsMatch.setMeanAbsoluteErrorMatchResult(meanAbsoluteErrorMatchResult);
        evaluationResultsDetailsMatch.setExpectedRootMeanSquaredError(expectedRootMeanSquaredError);
        evaluationResultsDetailsMatch.setActualRootMeanSquaredError(actualRootMeanSquaredError);
        evaluationResultsDetailsMatch.setRootMeanSquaredErrorMatchResult(rootMeanSquaredErrorMatchResult);
        evaluationResultsDetailsMatch.setExpectedMaxAucValue(expectedMaxAucValue);
        evaluationResultsDetailsMatch.setActualMaxAucValue(actualMaxAucValue);
        evaluationResultsDetailsMatch.setMaxAucValueMatchResult(maxAucValueMatchResult);
        if (evaluation.isKCrossValidationMethod()) {
            compareAndMatchConfidenceInterval(evaluation, statisticsReport, matcher, evaluationResultsDetailsMatch);
        }
    }

    private void compareAndMatchConfidenceInterval(Evaluation evaluation,
                                                   StatisticsReport statisticsReport,
                                                   TestResultsMatcher matcher,
                                                   EvaluationResultsDetailsMatch evaluationResultsDetailsMatch) {
        BigDecimal expectedVarianceError = getScaledValue(evaluation.varianceError(), SCALE);
        BigDecimal actualVarianceError = getScaledValue(statisticsReport.getVarianceError(), SCALE);
        MatchResult varianceErrorMatchResult = matcher.compareAndMatch(expectedVarianceError, actualVarianceError);

        double[] errorConfidenceInterval = evaluation.errorConfidenceInterval();

        BigDecimal expectedConfidenceIntervalLowerBound =
                getScaledValue(errorConfidenceInterval[CONFIDENCE_INTERVAL_LOWER_INDEX], SCALE);
        BigDecimal actualConfidenceIntervalLowerBound = statisticsReport.getConfidenceIntervalLowerBound();
        MatchResult confidenceIntervalLowerBoundMatchResult =
                matcher.compareAndMatch(expectedConfidenceIntervalLowerBound, actualConfidenceIntervalLowerBound);

        BigDecimal expectedConfidenceIntervalUpperBound =
                getScaledValue(errorConfidenceInterval[CONFIDENCE_INTERVAL_UPPER_INDEX], SCALE);
        BigDecimal actualConfidenceIntervalUpperBound = statisticsReport.getConfidenceIntervalUpperBound();
        MatchResult confidenceIntervalUpperBoundMatchResult =
                matcher.compareAndMatch(expectedConfidenceIntervalUpperBound, actualConfidenceIntervalUpperBound);

        evaluationResultsDetailsMatch.setExpectedVarianceError(expectedVarianceError);
        evaluationResultsDetailsMatch.setActualVarianceError(actualVarianceError);
        evaluationResultsDetailsMatch.setVarianceErrorMatchResult(varianceErrorMatchResult);
        evaluationResultsDetailsMatch.setExpectedConfidenceIntervalLowerBound(expectedConfidenceIntervalLowerBound);
        evaluationResultsDetailsMatch.setActualConfidenceIntervalLowerBound(actualConfidenceIntervalLowerBound);
        evaluationResultsDetailsMatch.setConfidenceIntervalLowerBoundMatchResult(
                confidenceIntervalLowerBoundMatchResult);
        evaluationResultsDetailsMatch.setExpectedConfidenceIntervalUpperBound(expectedConfidenceIntervalUpperBound);
        evaluationResultsDetailsMatch.setActualConfidenceIntervalUpperBound(actualConfidenceIntervalUpperBound);
        evaluationResultsDetailsMatch.setConfidenceIntervalUpperBoundMatchResult(
                confidenceIntervalUpperBoundMatchResult);
    }

    private void compareAndMatchConfusionMatrix(Evaluation evaluation,
                                                GetEvaluationResultsResponse evaluationResultsResponse,
                                                TestResultsMatcher matcher,
                                                EvaluationResultsDetailsMatch evaluationResultsDetailsMatch) {
        double[][] confusionMatrix = evaluation.confusionMatrix();
        List<ConfusionMatrixReport> confusionMatrixReports = evaluationResultsResponse.getConfusionMatrix();
        Attribute classAttribute = evaluation.getData().classAttribute();
        int expectedConfusionMatrixDimension = confusionMatrix.length * confusionMatrix[0].length;
        if (!Objects.equals(expectedConfusionMatrixDimension, confusionMatrixReports.size())) {
            String errorMessage =
                    String.format(INVALID_CONFUSION_MATRIX_SIZE_FORMAT, evaluationResultsResponse.getRequestId(),
                            expectedConfusionMatrixDimension, confusionMatrixReports.size());
            throw new IllegalStateException(errorMessage);
        }
        List<ConfusionMatrixDetailsMatch> confusionMatrixDetailsMatches = newArrayList();
        IntStream.range(0, confusionMatrix.length)
                .forEach(i -> IntStream.range(0, confusionMatrix[i].length)
                        .forEach(j -> {
                            String actualClassValue = classAttribute.value(i);
                            String predictedClassValue = classAttribute.value(j);
                            var reports = confusionMatrixReports
                                    .stream()
                                    .filter(cmr -> actualClassValue.equals(cmr.getActualClass()) &&
                                            predictedClassValue.equals(cmr.getPredictedClass()))
                                    .collect(Collectors.toList());
                            Assert.state(reports.size() == 1,
                                    String.format(CONFUSION_MATRIX_ERROR_MESSAGE_FORMAT,
                                    evaluationResultsResponse.getRequestId(), actualClassValue, predictedClassValue));
                            var confusionMatrixReport = reports.iterator().next();
                            var confusionMatrixDetailsMatch =
                                    compareAndMatchConfusionMatrix(actualClassValue, predictedClassValue,
                                            confusionMatrix[i][j], confusionMatrixReport, matcher);
                            confusionMatrixDetailsMatches.add(confusionMatrixDetailsMatch);
                        })
                );
        evaluationResultsDetailsMatch.setConfusionMatrixDetails(confusionMatrixDetailsMatches);
    }

    private ConfusionMatrixDetailsMatch compareAndMatchConfusionMatrix(String expectedActualClassValue,
                                                                       String expectedPredictedClassValue,
                                                                       double numInstances,
                                                                       ConfusionMatrixReport confusionMatrixReport,
                                                                       TestResultsMatcher matcher) {
        var confusionMatrixDetailsMatch = new ConfusionMatrixDetailsMatch();

        String actualClassValue = confusionMatrixReport.getActualClass();
        confusionMatrixDetailsMatch.setExpectedActualClassValue(expectedActualClassValue);
        confusionMatrixDetailsMatch.setActualClassValue(actualClassValue);
        MatchResult actualClassValueMatchResult = matcher.compareAndMatch(expectedActualClassValue, actualClassValue);
        confusionMatrixDetailsMatch.setActualClassValueMatchResult(actualClassValueMatchResult);

        String actualPredictedClassValue = confusionMatrixReport.getPredictedClass();
        confusionMatrixDetailsMatch.setExpectedPredictedClassValue(expectedPredictedClassValue);
        confusionMatrixDetailsMatch.setActualPredictedClassValue(actualPredictedClassValue);
        MatchResult predictedClassValueMatchResult =
                matcher.compareAndMatch(expectedPredictedClassValue, actualPredictedClassValue);
        confusionMatrixDetailsMatch.setPredictedClassValueMatchResult(predictedClassValueMatchResult);

        BigInteger expectedNumInstances = BigInteger.valueOf((long) numInstances);
        BigInteger actualNumInstances = confusionMatrixReport.getNumInstances();
        confusionMatrixDetailsMatch.setExpectedNumInstances(expectedNumInstances);
        confusionMatrixDetailsMatch.setActualNumInstances(actualNumInstances);
        MatchResult numInstancesMatchResult = matcher.compareAndMatch(expectedNumInstances, actualNumInstances);
        confusionMatrixDetailsMatch.setNumInstancesMatchResult(numInstancesMatchResult);

        return confusionMatrixDetailsMatch;
    }
}
