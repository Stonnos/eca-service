package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

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
    @Schema(description = "ERS request date", type = "string", example = "2021-07-01 14:00:00")
    private LocalDateTime requestDate;

    /**
     * Request id
     */
    @Schema(description = "Request id")
    private String requestId;

    /**
     * Training data name
     */
    @Schema(description = "Training data name")
    private String relationName;

    /**
     * Evaluation method
     */
    @Schema(description = "Evaluation method")
    private EnumDto evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @Schema(description = "Folds number for k * V cross - validation method")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @Schema(description = "Tests number for k * V cross - validation method")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @Schema(description = "Seed value for k * V cross - validation method")
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
