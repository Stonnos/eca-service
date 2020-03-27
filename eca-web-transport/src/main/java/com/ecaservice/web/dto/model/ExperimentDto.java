package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Experiment dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Experiment model")
public class ExperimentDto {

    /**
     * First name
     */
    @ApiModelProperty(value = "Request creator first name")
    private String firstName;

    /**
     * Email
     */
    @ApiModelProperty(value = "Request creator email")
    private String email;

    /**
     * Experiment file absolute path
     */
    @ApiModelProperty(value = "Experiment results file")
    private String experimentAbsolutePath;

    /**
     * Training data absolute path
     */
    @ApiModelProperty(value = "Training data file")
    private String trainingDataAbsolutePath;

    /**
     * Experiment request id
     */
    @ApiModelProperty(value = "Experiment request id")
    private String requestId;

    /**
     * Request creation date
     */
    @ApiModelProperty(value = "Experiment creation date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Experiment processing start date
     */
    @ApiModelProperty(value = "Experiment processing start date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    /**
     * Experiment processing end date
     */
    @ApiModelProperty(value = "Experiment processing end date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    /**
     * Date when experiment results is sent
     */
    @ApiModelProperty(value = "Experiment results sent date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime sentDate;

    /**
     * Experiment files deleted date
     */
    @ApiModelProperty(value = "Experiment files delete date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime deletedDate;

    /**
     * Experiment type
     */
    @ApiModelProperty(value = "Experiment type")
    private EnumDto experimentType;

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
}
