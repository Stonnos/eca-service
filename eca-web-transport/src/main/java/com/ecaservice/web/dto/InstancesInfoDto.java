package com.ecaservice.web.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Instances info dto model.
 *
 * @author Roman Batygin
 */
@Data
public class InstancesInfoDto {

    /**
     * Instances name
     */
    @ApiModelProperty(notes = "Instances name")
    private String relationName;

    /**
     * Instances size
     */
    @ApiModelProperty(notes = "Instances number")
    private Integer numInstances;

    /**
     * Attributes number
     */
    @ApiModelProperty(notes = "Attributes number")
    private Integer numAttributes;

    /**
     * Classes number
     */
    @ApiModelProperty(notes = "Classes number")
    private Integer numClasses;

    /**
     * Class attribute name
     */
    @ApiModelProperty(notes = "Class name")
    private String className;
}
