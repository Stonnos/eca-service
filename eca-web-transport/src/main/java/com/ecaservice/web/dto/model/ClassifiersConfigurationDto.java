package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Classifiers configuration dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classifiers configuration dto model")
public class ClassifiersConfigurationDto {

    @ApiModelProperty(value = "Configuration id")
    private Long id;

    /**
     * Configuration name
     */
    @ApiModelProperty(value = "Configuration name")
    private String configurationName;

    /**
     * Configuration created date
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(value = "Configuration creation date")
    private LocalDateTime created;

    /**
     * Configuration updated date
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @ApiModelProperty(value = "Configuration updated date")
    private LocalDateTime updated;

    /**
     * Is active?
     */
    @ApiModelProperty(value = "Is active?")
    private boolean active;

    /**
     * Is build in?
     */
    @ApiModelProperty(value = "Is build in?")
    private boolean buildIn;

    /**
     * Classifiers options count associated with configuration
     */
    @ApiModelProperty(value = "Classifiers options count associated with configuration")
    private long classifiersOptionsCount;
}
