package com.ecaservice.web.dto.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Classifier input options dto model.
 *
 * @author Roman Batygin
 */
@Data
public class ClassifierOptionsDto {

    /**
     * Config version
     */
    @ApiModelProperty(notes = "Options version", required = true)
    private int version;

    /**
     * Options name
     */
    private String optionsName;

    /**
     * Creation date
     */
    @ApiModelProperty(notes = "Creation date", required = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime creationDate;

    /**
     * Json config
     */
    @ApiModelProperty(notes = "Json config", required = true)
    private String config;
}
