package com.ecaservice.dto;

import com.ecaservice.dto.json.InstancesDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import weka.core.Instances;

/**
 * Instances request dto.
 *
 * @author Roman Batygin
 */
@Data
public class InstancesRequest {

    /**
     * Training data
     */
    @ApiModelProperty(notes = "Training data", required = true)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;
}
