package com.ecaservice.base.model;

import com.ecaservice.base.model.databind.InstancesDeserializer;
import com.ecaservice.base.model.databind.InstancesSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.core.Instances;

import javax.validation.constraints.NotNull;

/**
 * Instances request dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstancesRequest {

    /**
     * Training data
     */
    @NotNull
    @JsonSerialize(using = InstancesSerializer.class)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;
}
