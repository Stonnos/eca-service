package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "Request unique identifier")
    private String requestId;

    /**
     * Request creation date
     */
    @ApiModelProperty(value = "Request creation date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Evaluation start date
     */
    @ApiModelProperty(value = "Evaluation start date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    /**
     * Evaluation end date
     */
    @ApiModelProperty(value = "Evaluation end date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    /**
     * Request status
     */
    @ApiModelProperty(value = "Request status")
    private EnumDto requestStatus;

    /**
     * Evaluation method
     */
    @ApiModelProperty(value = "Evaluation method")
    private EnumDto evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @ApiModelProperty(value = "Folds number for k * V cross - validation method")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @ApiModelProperty(value = "Tests number for k * V cross - validation method")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @ApiModelProperty(value = "Seed value for k * V cross - validation method")
    private Integer seed;

    /**
     * Model evaluation total time in format HH:mm:ss:SS
     */
    @ApiModelProperty(value = "Model evaluation total time in format HH:mm:ss:SS")
    private String evaluationTotalTime;
}
