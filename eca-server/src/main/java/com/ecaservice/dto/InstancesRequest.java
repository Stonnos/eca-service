package com.ecaservice.dto;

import com.ecaservice.dto.json.InstancesDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.core.Instances;

/**
 * Instances request dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Training data request model")
public class InstancesRequest {

    /**
     * Training data
     */
    @ApiModelProperty(value = "Training data", required = true)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;
}
