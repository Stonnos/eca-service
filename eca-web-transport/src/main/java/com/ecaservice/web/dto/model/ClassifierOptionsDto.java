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
 * Classifier input options dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classifier json input options model")
public class ClassifierOptionsDto {

    @ApiModelProperty(value = "Options id")
    private Long id;

    /**
     * Options name
     */
    @ApiModelProperty(value = "Options name", required = true)
    private String optionsName;

    /**
     * Creation date
     */
    @ApiModelProperty(value = "Creation date", required = true)
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Json config
     */
    @ApiModelProperty(value = "Json config", required = true)
    private String config;
}
