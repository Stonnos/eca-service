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

    /**
     * Train data url in internal format data://dataId
     */
    @ApiModelProperty(value = "Train data url in internal format data://dataId")
    private String dataUrl;

    /**
     * Request status
     */
    @ApiModelProperty(value = "Request status")
    private RequestStatus status;
}
