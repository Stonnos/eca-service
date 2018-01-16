package com.ecaservice.dto;

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

    @ApiModelProperty(notes = "Options version", required = true)
    private int version;

    @ApiModelProperty(notes = "Creation date", required = true)
    private LocalDateTime creationDate;

    @ApiModelProperty(notes = "Json config", required = true)
    private String config;
}
