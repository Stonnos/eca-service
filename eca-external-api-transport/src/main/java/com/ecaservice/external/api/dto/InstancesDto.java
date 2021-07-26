package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Instances model")
public class InstancesDto implements Serializable {

    /**
     * Train data id
     */
    @Schema(description = "Data id")
    private String dataId;

    /**
     * Train data url in internal format data://dataId
     */
    @Schema(description = "Train data url in internal format data://dataId")
    private String dataUrl;
}
