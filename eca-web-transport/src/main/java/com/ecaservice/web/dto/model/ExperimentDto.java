package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Experiment dto model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Experiment model")
public class ExperimentDto extends AbstractEvaluationDto {

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
     * Date when experiment results is sent
     */
    @ApiModelProperty(value = "Experiment results sent date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime sentDate;

    /**
     * Experiment files deleted date
     */
    @ApiModelProperty(value = "Experiment files delete date")
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime deletedDate;

    /**
     * Experiment type
     */
    @ApiModelProperty(value = "Experiment type")
    private EnumDto experimentType;
}