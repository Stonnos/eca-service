package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;
import static com.ecaservice.web.dto.util.FieldConstraints.LOCAL_DATE_TIME_MAX_LENGTH;
import static com.ecaservice.web.dto.util.FieldConstraints.MAXIMUM_NUM_FOLDS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAXIMUM_NUM_TESTS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.MINIMUM_NUM_FOLDS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.MINIMUM_NUM_TESTS_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;

/**
 * Classifier options request dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "ERS classifier options request model")
public class ClassifierOptionsRequestDto {

    /**
     * ERS - service request date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @Schema(description = "ERS request date", type = "string", example = "2021-07-01 14:00:00", maxLength = LOCAL_DATE_TIME_MAX_LENGTH)
    private LocalDateTime requestDate;

    /**
     * Request id
     */
    @Schema(description = "Request id", example = "1d2de514-3a87-4620-9b97-c260e24340de", maxLength = UUID_MAX_LENGTH)
    private String requestId;

    /**
     * Training data name
     */
    @Schema(description = "Training data name", example = "glass", maxLength = MAX_LENGTH_255)
    private String relationName;

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
    @Schema(description = "Seed value for k * V cross - validation method", example = "1")
    private Integer seed;

    /**
     * Response status from ERS - service
     */
    @Schema(description = "Response status from ERS - service")
    private EnumDto responseStatus;

    /**
     * Classifiers options response models.
     */
    @Schema(description = "Classifiers options response models")
    private List<ClassifierOptionsResponseDto> classifierOptionsResponseModels;
}
