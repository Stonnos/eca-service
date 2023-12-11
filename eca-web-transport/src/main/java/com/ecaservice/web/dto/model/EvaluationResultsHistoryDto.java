package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.MAXIMUM_NUM_FOLDS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAXIMUM_NUM_TESTS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MINIMUM_NUM_FOLDS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MINIMUM_NUM_TESTS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MIN_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_100_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_2_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Classifier evaluation results history model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier evaluation results history model")
public class EvaluationResultsHistoryDto {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Classifier info
     */
    @Schema(description = "Classifier info")
    private ClassifierInfoDto classifierInfo;

    /**
     * Training data info
     */
    @Schema(description = "Training data info")
    private InstancesInfoDto instancesInfo;

    /**
     * Evaluation method
     */
    @Schema(description = "Evaluation method")
    private EnumDto evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Schema(description = "Folds number for k * V cross - validation method", example = "10",
            minimum = MINIMUM_NUM_FOLDS_STRING, maximum = MAXIMUM_NUM_FOLDS_STRING)
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Schema(description = "Tests number for k * V cross - validation method", example = "1",
            minimum = MINIMUM_NUM_TESTS_STRING, maximum = MAXIMUM_NUM_TESTS_STRING)
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @Schema(description = "Seed value for k * V cross - validation method", example = "1",
            minimum = MIN_INTEGER_VALUE_STRING, maximum = MAX_INTEGER_VALUE_STRING)
    private Integer seed;

    /**
     * Save date
     */
    @Schema(description = "Save date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime saveDate;

    /**
     * Test instances number
     */
    @Schema(description = "Test instances number", example = "150", minimum = VALUE_2_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private BigInteger numTestInstances;

    /**
     * Correctly classified instances number
     */
    @Schema(description = "Correctly classified instances number", example = "146", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private BigInteger numCorrect;

    /**
     * Incorrectly classified instances number
     */
    @Schema(description = "Incorrectly classified instances number", example = "4", minimum = ZERO_VALUE_STRING,
            maximum = MAX_INTEGER_VALUE_STRING)
    private BigInteger numIncorrect;

    /**
     * Correctly classified percentage
     */
    @Schema(description = "Correctly classified percentage", example = "96", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_100_STRING)
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    @Schema(description = "Incorrectly classified percentage", example = "4", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_100_STRING)
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    @Schema(description = "Mean absolute error", example = "0.29", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_1_STRING)
    private BigDecimal meanAbsoluteError;

    /**
     * Root mean squared error
     */
    @Schema(description = "Root mean squared error", example = "0.01", minimum = ZERO_VALUE_STRING,
            maximum = VALUE_1_STRING)
    private BigDecimal rootMeanSquaredError;

    /**
     * Max AUC value
     */
    @Schema(description = "Max AUC value", example = "0.89", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal maxAucValue;

    /**
     * Variance error
     */
    @Schema(description = "Variance error", example = "0.0012", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal varianceError;

    /**
     * 95% confidence interval lower bound value
     */
    @Schema(description = "95% confidence interval lower bound value", example = "0.01",
            minimum = MIN_INTEGER_VALUE_STRING, maximum = MAX_INTEGER_VALUE_STRING)
    private BigDecimal confidenceIntervalLowerBound;

    /**
     * 95% confidence interval upper bound value
     */
    @Schema(description = "95% confidence interval upper bound value", example = "0.035",
            minimum = MIN_INTEGER_VALUE_STRING, maximum = MAX_INTEGER_VALUE_STRING)
    private BigDecimal confidenceIntervalUpperBound;
}
