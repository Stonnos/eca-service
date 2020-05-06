package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "ERS classifier options request model")
public class ClassifierOptionsRequestDto {

    /**
     * ERS - service request date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(value = "ERS request date")
    private LocalDateTime requestDate;

    /**
     * Request id
     */
    @ApiModelProperty(value = "Request id")
    private String requestId;

    /**
     * Training data name
     */
    @ApiModelProperty(value = "Training data name")
    private String relationName;

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
     * Response status from ERS - service
     */
    @ApiModelProperty(value = "Response status from ERS - service")
    private EnumDto responseStatus;

    /**
     * Classifiers options response models.
     */
    @ApiModelProperty(value = "Classifiers options response models")
    private List<ClassifierOptionsResponseDto> classifierOptionsResponseModels;
}
