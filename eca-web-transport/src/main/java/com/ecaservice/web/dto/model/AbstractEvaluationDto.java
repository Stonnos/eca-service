package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Abstract evaluation dto model.
 *
 * @author Roman Batygin
 */
@Data
public abstract class AbstractEvaluationDto {

    /**
     * Request unique identifier
     */
    @Schema(description = "Request unique identifier")
    private String requestId;

    /**
     * Request creation date
     */
    @Schema(description = "Request creation date", type = "string", example = "2021-07-01 14:00:00")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Evaluation start date
     */
    @Schema(description = "Evaluation start date", type = "string", example = "2021-07-01 14:00:00")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    /**
     * Evaluation end date
     */
    @Schema(description = "Evaluation end date", type = "string", example = "2021-07-01 14:00:00")
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
     * Model evaluation total time in format HH:mm:ss:SS
     */
    @Schema(description = "Model evaluation total time in format HH:mm:ss:SS")
    private String evaluationTotalTime;
}
