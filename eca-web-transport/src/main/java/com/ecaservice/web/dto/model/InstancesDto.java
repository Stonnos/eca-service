package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

import static com.ecaservice.web.dto.util.FieldConstraints.DATE_TIME_PATTERN;

/**
 * Instances dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Instances model")
public class InstancesDto {

    @ApiModelProperty(value = "Instances id")
    private Long id;

    /**
     * Instances name
     */
    @ApiModelProperty(value = "Table name")
    private String tableName;

    /**
     * Instances size
     */
    @ApiModelProperty(value = "Instances number")
    private Integer numInstances;

    /**
     * Attributes number
     */
    @ApiModelProperty(value = "Attributes number")
    private Integer numAttributes;

    /**
     * Instances created date
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(value = "Instances creation date")
    private LocalDateTime created;

    /**
     * User name
     */
    @ApiModelProperty(value = "User name")
    private String createdBy;
}
