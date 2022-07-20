package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.EVALUATION_TOTAL_TIME_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.MAXIMUM_NUM_FOLDS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAXIMUM_NUM_TESTS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LONG_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MINIMUM_NUM_FOLDS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MINIMUM_NUM_TESTS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MIN_INTEGER_VALUE_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;

/**
 * Abstract evaluation dto model.
 *
 * @author Roman Batygin
 */
@Data
public abstract class AbstractEvaluationDto {

    /**
     * ID
     */
    @Schema(description = "ID", example = "1", minimum = VALUE_1_STRING, maximum = MAX_LONG_VALUE_STRING)
    private Long id;

    /**
     * Request unique identifier
     */
    @Schema(description = "Request unique identifier", example = "1d2de514-3a87-4620-9b97-c260e24340de",
            maxLength = UUID_MAX_LENGTH)
    private String requestId;

    /**
     * Training data info
     */
    @Schema(description = "Training data info")
    private InstancesInfoDto instancesInfo;

    /**
     * Request creation date
     */
    @Schema(description = "Request creation date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Evaluation start date
     */
    @Schema(description = "Evaluation start date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    /**
     * Evaluation end date
     */
    @Schema(description = "Evaluation end date", type = "string", example = "2021-07-01 14:00:00",
            maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    /**
     * Request status
     */
    @Schema(description = "Request status")
    private EnumDto requestStatus;

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
     * Model evaluation total time in format HH:mm:ss:SS
     */
    @Schema(description = "Model evaluation total time in format HH:mm:ss:SS", example = "00:00:01:43",
            maxLength = EVALUATION_TOTAL_TIME_MAX_LENGTH)
    private String evaluationTotalTime;
}
