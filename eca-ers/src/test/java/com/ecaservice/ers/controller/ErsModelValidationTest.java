package com.ecaservice.ers.controller;

import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.google.common.collect.ImmutableList;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static com.ecaservice.ers.TestHelperUtils.buildEvaluationResultsReport;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for checking ers dto validation.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
class ErsModelValidationTest {

    private static final int LARGE_STRING_LENGTH = 256;
    private static final BigDecimal NEGATIVE_VALUE = BigDecimal.valueOf(-1L);

    /**
     * Required fields for tests
     */
    private static final List<String> EVALUATION_REQUEST_NULL_TEST =
            ImmutableList.of("requestId", "instances", "classifierReport", "evaluationMethodReport", "statistics");
    private static final List<String> CLASSIFIER_FIELDS_NULL_TEST =
            ImmutableList.of("classifierName", "options");
    private static final List<String> INSTANCES_FIELDS_NULL_TEST =
            ImmutableList.of("structure", "relationName", "numInstances", "numAttributes", "numClasses",
                    "className");
    private static final List<String> EVALUATION_METHOD_REPORT_FIELDS_NULL_TEST =
            ImmutableList.of("evaluationMethod");
    private static final List<String> STATISTICS_FIELDS_NULL_TEST =
            ImmutableList.of("numTestInstances", "numCorrect", "numIncorrect", "pctCorrect", "pctIncorrect");
    private static final List<String> CLASSIFICATION_COSTS_FIELDS_NULL_TEST =
            ImmutableList.of("classValue", "truePositiveRate", "falsePositiveRate", "trueNegativeRate",
                    "falseNegativeRate");
    private static final List<String> CONFUSION_MATRIX_FIELDS_NULL_TEST =
            ImmutableList.of("actualClass", "predictedClass", "numInstances");
    private static final List<String> INPUT_OPTIONS_MAP_NULL_TEST =
            ImmutableList.of("key", "value");

    /**
     * Not empty string fields for tests
     */
    private static final List<String> CLASSIFIER_FIELDS_EMPTY_TEST =
            ImmutableList.of("classifierName", "options");
    private static final List<String> INSTANCES_FIELDS_EMPTY_TEST =
            ImmutableList.of("structure", "relationName", "className");
    private static final List<String> CLASSIFICATION_COSTS_FIELDS_EMPTY_TEST =
            ImmutableList.of("classValue");
    private static final List<String> CONFUSION_MATRIX_FIELDS_EMPTY_TEST =
            ImmutableList.of("actualClass", "predictedClass");
    private static final List<String> INPUT_OPTIONS_MAP_EMPTY_TEST =
            ImmutableList.of("key", "value");

    /**
     * Not large string fields to tests
     */
    private static final List<String> CLASSIFIER_FIELDS_LARGE_TEST =
            ImmutableList.of("classifierName", "classifierDescription");
    private static final List<String> INSTANCES_FIELDS_LARGE_TEST =
            ImmutableList.of("relationName", "className");
    private static final List<String> CLASSIFICATION_COSTS_FIELDS_LARGE_TEST =
            ImmutableList.of("classValue");
    private static final List<String> CONFUSION_MATRIX_FIELDS_LARGE_TEST =
            ImmutableList.of("actualClass", "predictedClass");
    private static final List<String> INPUT_OPTIONS_MAP_LARGE_TEST =
            ImmutableList.of("key", "value");

    /**
     * Decimal fields to tests
     */
    private static final List<String> STATISTICS_PERCENTAGE_FIELDS_BOUNDS_TEST =
            ImmutableList.of("pctCorrect", "pctIncorrect");
    private static final List<String> STATISTICS_DECIMAL_FIELDS_BOUNDS_TEST =
            ImmutableList.of("meanAbsoluteError", "rootMeanSquaredError", "maxAucValue", "varianceError");
    private static final List<String> CLASSIFICATION_COSTS_FIELDS_BOUNDS_TEST =
            ImmutableList.of("truePositiveRate", "falsePositiveRate", "trueNegativeRate", "falseNegativeRate");
    private static final List<String> ROC_CURVE_FIELDS_BOUNDS_TEST =
            ImmutableList.of("aucValue", "specificity", "sensitivity", "thresholdValue");

    private final PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();

    private Validator validator;

    @BeforeEach
    void init() {
        validator = buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidateEvaluationReportWithNullFields() {
        internalTestNullFields(EVALUATION_REQUEST_NULL_TEST, Function.identity());
    }

    @Test
    void testValidateEvaluationReportWithInvalidRequestId() {
        internalTestFieldsWithConstraints(Collections.singletonList("requestId"), Function.identity(), "test-uuid");
    }

    @Test
    void testValidateEvaluationReportWithInvalidNumFolds() {
        internalTestFieldsWithConstraints(Collections.singletonList("numFolds"),
                EvaluationResultsRequest::getEvaluationMethodReport, BigDecimal.ONE.toBigInteger());
    }

    @Test
    void testValidateEvaluationReportWithInvalidNumTests() {
        internalTestFieldsWithConstraints(Collections.singletonList("numTests"),
                EvaluationResultsRequest::getEvaluationMethodReport, BigDecimal.ZERO.toBigInteger());
    }

    @Test
    void testValidateEvaluationReportWithInvalidNumInstances() {
        internalTestFieldsWithConstraints(Collections.singletonList("numInstances"),
                EvaluationResultsRequest::getInstances, BigDecimal.ONE.toBigInteger());
    }

    @Test
    void testValidateEvaluationReportWithInvalidNumAttributes() {
        internalTestFieldsWithConstraints(Collections.singletonList("numAttributes"),
                EvaluationResultsRequest::getInstances, BigDecimal.ONE.toBigInteger());
    }

    @Test
    void testValidateEvaluationReportWithInvalidNumClasses() {
        internalTestFieldsWithConstraints(Collections.singletonList("numClasses"),
                EvaluationResultsRequest::getInstances, BigDecimal.ONE.toBigInteger());
    }

    @Test
    void testValidateEvaluationReportWithInvalidNumTestInstances() {
        internalTestFieldsWithConstraints(Collections.singletonList("numTestInstances"),
                EvaluationResultsRequest::getStatistics, BigDecimal.ONE.toBigInteger());
    }

    @Test
    void testValidateEvaluationReportWithInvalidNumCorrect() {
        internalTestFieldsWithConstraints(Collections.singletonList("numCorrect"),
                EvaluationResultsRequest::getStatistics, NEGATIVE_VALUE.toBigInteger());
    }

    @Test
    void testValidateEvaluationReportWithInvalidNumIncorrect() {
        internalTestFieldsWithConstraints(Collections.singletonList("numIncorrect"),
                EvaluationResultsRequest::getStatistics, NEGATIVE_VALUE.toBigInteger());
    }

    @Test
    void testValidateEvaluationReportWithInvalidConfusionMatrixNumInstances() {
        internalTestFieldsWithConstraints(Collections.singletonList("numInstances"),
                request -> request.getConfusionMatrix().iterator().next(), NEGATIVE_VALUE.toBigInteger());
    }

    @Test
    void testValidateEvaluationReportWithEmptyClassifierReportFields() {
        internalTestEmptyFields(CLASSIFIER_FIELDS_EMPTY_TEST, EvaluationResultsRequest::getClassifierReport);
    }

    @Test
    void testValidateEvaluationReportWithEmptyInstancesReportFields() {
        internalTestEmptyFields(INSTANCES_FIELDS_EMPTY_TEST, EvaluationResultsRequest::getInstances);
    }

    @Test
    void testValidateEvaluationReportWithLargeClassifierReportFields() {
        internalTestLargeFields(CLASSIFIER_FIELDS_LARGE_TEST, EvaluationResultsRequest::getClassifierReport);
    }

    @Test
    void testValidateEvaluationReportWithLargeInstancesReportFields() {
        internalTestLargeFields(INSTANCES_FIELDS_LARGE_TEST, EvaluationResultsRequest::getInstances);
    }

    @Test
    void testValidateEvaluationReportWithNotValidPercentageFields() {
        internalTestFieldsWithConstraints(STATISTICS_PERCENTAGE_FIELDS_BOUNDS_TEST,
                EvaluationResultsRequest::getStatistics, NEGATIVE_VALUE);
        internalTestFieldsWithConstraints(STATISTICS_PERCENTAGE_FIELDS_BOUNDS_TEST,
                EvaluationResultsRequest::getStatistics, BigDecimal.valueOf(101L));
    }

    @Test
    void testValidateEvaluationReportWithNotValidStatisticsDecimalFields() {
        internalTestFieldsWithConstraints(STATISTICS_DECIMAL_FIELDS_BOUNDS_TEST,
                EvaluationResultsRequest::getStatistics, NEGATIVE_VALUE);
        internalTestFieldsWithConstraints(STATISTICS_DECIMAL_FIELDS_BOUNDS_TEST,
                EvaluationResultsRequest::getStatistics, BigDecimal.valueOf(1.01d));
    }

    @Test
    void testValidateEvaluationReportWithNullInstancesReportFields() {
        internalTestNullFields(INSTANCES_FIELDS_NULL_TEST, EvaluationResultsRequest::getInstances);
    }

    @Test
    void testValidateEvaluationReportWithNullClassifierReportFields() {
        internalTestNullFields(CLASSIFIER_FIELDS_NULL_TEST, EvaluationResultsRequest::getClassifierReport);
    }

    @Test
    void testValidateEvaluationReportWithNullEvaluationMethodReportFields() {
        internalTestNullFields(EVALUATION_METHOD_REPORT_FIELDS_NULL_TEST,
                EvaluationResultsRequest::getEvaluationMethodReport);
    }

    @Test
    void testValidateEvaluationReportWithNullStatisticsReportFields() {
        internalTestNullFields(STATISTICS_FIELDS_NULL_TEST, EvaluationResultsRequest::getStatistics);
    }

    @Test
    void testValidateEvaluationReportWithNullClassificationCostsRecordFields() {
        internalTestNullFields(CLASSIFICATION_COSTS_FIELDS_NULL_TEST,
                (request) -> request.getClassificationCosts().iterator().next());
    }

    @Test
    void testValidateEvaluationReportWithNullConfusionMatrixRecordFields() {
        internalTestNullFields(CONFUSION_MATRIX_FIELDS_NULL_TEST,
                (request) -> request.getConfusionMatrix().iterator().next());
    }

    @Test
    void testValidateEvaluationReportWithEmptyClassificationCostsRecordFields() {
        internalTestEmptyFields(CLASSIFICATION_COSTS_FIELDS_EMPTY_TEST,
                (request) -> request.getClassificationCosts().iterator().next());
    }

    @Test
    void testValidateEvaluationReportWithEmptyConfusionMatrixRecordFields() {
        internalTestEmptyFields(CONFUSION_MATRIX_FIELDS_EMPTY_TEST,
                (request) -> request.getConfusionMatrix().iterator().next());
    }

    @Test
    void testValidateEvaluationReportWithLargeClassificationCostsRecordFields() {
        internalTestLargeFields(CLASSIFICATION_COSTS_FIELDS_LARGE_TEST,
                (request) -> request.getClassificationCosts().iterator().next());
    }

    @Test
    void testValidateEvaluationReportWithLargeConfusionMatrixRecordFields() {
        internalTestLargeFields(CONFUSION_MATRIX_FIELDS_LARGE_TEST,
                (request) -> request.getConfusionMatrix().iterator().next());
    }

    @Test
    void testValidateEvaluationReportWithNotValidClassificationCostsRecordDecimalFields() {
        internalTestFieldsWithConstraints(CLASSIFICATION_COSTS_FIELDS_BOUNDS_TEST,
                (request) -> request.getClassificationCosts().iterator().next(), NEGATIVE_VALUE);
        internalTestFieldsWithConstraints(CLASSIFICATION_COSTS_FIELDS_BOUNDS_TEST,
                (request) -> request.getClassificationCosts().iterator().next(), BigDecimal.valueOf(1.01d));
    }

    @Test
    void testValidateEvaluationReportWithNotValidRocCurveReportDecimalFields() {
        internalTestFieldsWithConstraints(ROC_CURVE_FIELDS_BOUNDS_TEST,
                (request) -> request.getClassificationCosts().iterator().next().getRocCurve(), NEGATIVE_VALUE);
        internalTestFieldsWithConstraints(ROC_CURVE_FIELDS_BOUNDS_TEST,
                (request) -> request.getClassificationCosts().iterator().next().getRocCurve(),
                BigDecimal.valueOf(1.01d));
    }

    @Test
    void testValidateEvaluationReportWithNullInputOptionsMapFields() {
        internalTestNullFields(INPUT_OPTIONS_MAP_NULL_TEST,
                (request) -> request.getClassifierReport().getClassifierInputOptions().iterator().next());
    }

    @Test
    void testValidateEvaluationReportWithEmptyInputOptionsMapFields() {
        internalTestEmptyFields(INPUT_OPTIONS_MAP_EMPTY_TEST,
                (request) -> request.getClassifierReport().getClassifierInputOptions().iterator().next());
    }

    @Test
    void testValidateEvaluationReportWitLargeInputOptionsMapFields() {
        internalTestLargeFields(INPUT_OPTIONS_MAP_LARGE_TEST,
                (request) -> request.getClassifierReport().getClassifierInputOptions().iterator().next());
    }

    private <T> void internalTestEmptyFields(List<String> testFields,
                                             Function<EvaluationResultsRequest, T> targetFunction) {
        internalTestFieldsWithConstraints(testFields, targetFunction, StringUtils.EMPTY);
    }

    private <T> void internalTestNullFields(List<String> testFields,
                                            Function<EvaluationResultsRequest, T> targetFunction) {
        internalTestFieldsWithConstraints(testFields, targetFunction, null);
    }

    private <T> void internalTestLargeFields(List<String> testFields,
                                             Function<EvaluationResultsRequest, T> targetFunction) {
        internalTestFieldsWithConstraints(testFields, targetFunction, StringUtils.repeat('Q', LARGE_STRING_LENGTH));
    }

    private <T> void internalTestFieldsWithConstraints(List<String> testFields,
                                                       Function<EvaluationResultsRequest, T> targetFunction,
                                                       Object value) {
        for (String field : testFields) {
            try {
                EvaluationResultsRequest evaluationResultsRequest =
                        buildEvaluationResultsReport(UUID.randomUUID().toString());
                T target = targetFunction.apply(evaluationResultsRequest);
                propertyUtilsBean.setProperty(target, field, value);
                Set<ConstraintViolation<EvaluationResultsRequest>> violations =
                        validator.validate(evaluationResultsRequest);
                assertThat(violations.size()).isOne();
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
