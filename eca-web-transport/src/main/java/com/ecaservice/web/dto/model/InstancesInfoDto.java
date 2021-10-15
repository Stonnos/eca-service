package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Instances info dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Classifier training data model")
public class InstancesInfoDto {

    /**
     * Instances name
     */
    @Schema(description = "Instances name", example = "iris")
    private String relationName;

    /**
     * Instances size
     */
    @Schema(description = "Instances number", example = "150")
    private Integer numInstances;

    /**
     * Attributes number
     */
    @Schema(description = "Attributes number", example = "5")
    private Integer numAttributes;

    /**
     * Classes number
     */
    @Schema(description = "Classes number", example = "4")
    private Integer numClasses;

    /**
     * Class attribute name
     */
    @Schema(description = "Class name", example = "class")
    private String className;
}
