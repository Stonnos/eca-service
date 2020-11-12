package com.ecaservice.external.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * Instances dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class InstancesDto {

    /**
     * Train data id
     */
    @ApiModelProperty(value = "Data id")
    private String dataId;
}
