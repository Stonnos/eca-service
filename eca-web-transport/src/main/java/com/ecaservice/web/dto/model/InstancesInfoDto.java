package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Instances info dto model.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Classifier training data model")
public class InstancesInfoDto {

    /**
     * Instances name
     */
    @ApiModelProperty(value = "Instances name")
    private String relationName;

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
     * Classes number
     */
    @ApiModelProperty(value = "Classes number")
    private Integer numClasses;

    /**
     * Class attribute name
     */
    @ApiModelProperty(value = "Class name")
    private String className;
}
