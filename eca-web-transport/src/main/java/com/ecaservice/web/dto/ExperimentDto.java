package com.ecaservice.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Experiment dto model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentDto {

    /**
     * First name
     */
    @ApiModelProperty(notes = "Request creator first name")
    private String firstName;

    /**
     * Email
     */
    @ApiModelProperty(notes = "Request creator email")
    private String email;

    /**
     * Experiment file absolute path
     */
    @ApiModelProperty(notes = "Experiment results file")
    private String experimentAbsolutePath;

    /**
     * Training data absolute path
     */
    @ApiModelProperty(notes = "Training data file")
    private String trainingDataAbsolutePath;

    /**
     * Experiment uuid
     */
    @ApiModelProperty(notes = "Experiment uuid")
    private String uuid;

    /**
     * Request creation date
     */
    @ApiModelProperty(notes = "Experiment creation date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Experiment processing start date
     */
    @ApiModelProperty(notes = "Experiment processing start date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startDate;

    /**
     * Experiment processing end date
     */
    @ApiModelProperty(notes = "Experiment processing end date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime endDate;

    /**
     * Date when experiment results is sent
     */
    @ApiModelProperty(notes = "Experiment results sent date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime sentDate;

    /**
     * Experiment files deleted date
     */
    @ApiModelProperty(notes = "Experiment files delete date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime deletedDate;

    /**
     * Experiment type
     */
    @ApiModelProperty(notes = "Experiment type")
    private String experimentType;

    /**
     * Experiment status
     */
    @ApiModelProperty(notes = "Experiment status")
    private String experimentStatus;

    /**
     * Evaluation method
     */
    @ApiModelProperty(notes = "Evaluation method")
    private String evaluationMethod;
}
