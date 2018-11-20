package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
public class EvaluationLogDto {

    /**
     * Request unique identifier
     */
    @ApiModelProperty(notes = "Request unique identifier")
    private String requestId;

    /**
     * Request creation date
     */
    @ApiModelProperty(notes = "Request creation date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Evaluation start date
     */
    @ApiModelProperty(notes = "Evaluation start date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    /**
     * Evaluation end date
     */
    @ApiModelProperty(notes = "Evaluation end date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    /**
     * Classifier name
     */
    @ApiModelProperty(notes = "Classifier name")
    private String classifierName;

    /**
     * Evaluation status
     */
    @ApiModelProperty(notes = "Evaluation status")
    private String evaluationStatus;

    /**
     * Evaluation method
     */
    @ApiModelProperty(notes = "Evaluation method")
    private String evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    @ApiModelProperty(notes = "Folds number for k * V cross - validation method")
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    @ApiModelProperty(notes = "Tests number for k * V cross - validation method")
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    @ApiModelProperty(notes = "Seed value for k * V cross - validation method")
    private Integer seed;

    /**
     * Classifier input options map
     */
    @ApiModelProperty(notes = "Classifier input options map")
    private List<InputOptionDto> inputOptions;

    /**
     * Training data info
     */
    @ApiModelProperty(notes = "Training data info")
    private InstancesInfoDto instancesInfo;
}
