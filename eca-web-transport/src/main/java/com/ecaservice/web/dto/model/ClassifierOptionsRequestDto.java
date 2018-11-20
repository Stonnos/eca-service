package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Classifier options request dto model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierOptionsRequestDto {

    /**
     * ERS - service request date
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(notes = "ERS request date")
    private LocalDateTime requestDate;

    /**
     * Request id
     */
    @ApiModelProperty(notes = "Request id")
    private String requestId;

    /**
     * Training data name
     */
    @ApiModelProperty(notes = "Training data name")
    private String relationName;

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
     * Response status from ERS - service
     */
    @ApiModelProperty(notes = "Response status from ERS - service")
    private String responseStatus;

    /**
     * Classifiers options response models.
     */
    @ApiModelProperty(notes = "Classifiers options response models")
    private List<ClassifierOptionsResponseDto> classifierOptionsResponseModels;
}
