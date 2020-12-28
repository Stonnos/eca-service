package com.ecaservice.external.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Instances dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstancesDto implements Serializable {

    /**
     * Train data id
     */
    @ApiModelProperty(value = "Data id")
    private String dataId;

    /**
     * Train data url in internal format data://dataId
     */
    @ApiModelProperty(value = "Train data url in internal format data://dataId")
    private String dataUrl;
}
