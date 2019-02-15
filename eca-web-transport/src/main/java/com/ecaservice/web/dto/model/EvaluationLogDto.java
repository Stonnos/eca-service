package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Experiment dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classifier evaluation log model")
public class EvaluationLogDto {

    /**
     * Request unique identifier
     */
    @ApiModelProperty(value = "Request unique identifier")
    private String requestId;

    /**
     * Request creation date
     */
    @ApiModelProperty(value = "Request creation date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Evaluation start date
     */
    @ApiModelProperty(value = "Evaluation start date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    /**
     * Evaluation end date
     */
    @ApiModelProperty(value = "Evaluation end date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    /**
     * Classifier name
     */
    @ApiModelProperty(value = "Classifier name")
    private String classifierName;

    /**
     * Evaluation status
     */
    @ApiModelProperty(value = "Evaluation status")
    private String evaluationStatus;

    /**
     * Evaluation method
     */
    @ApiModelProperty(value = "Evaluation method")
    private String evaluationMethod;

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
     * Classifier input options map
     */
    @ApiModelProperty(value = "Classifier input options map")
    private List<InputOptionDto> inputOptions;

    /**
     * Training data info
     */
    @ApiModelProperty(value = "Training data info")
    private InstancesInfoDto instancesInfo;
}
