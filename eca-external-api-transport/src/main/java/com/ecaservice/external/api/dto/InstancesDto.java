package com.ecaservice.external.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.external.api.dto.Constraints.UUID_MAX_LENGTH;

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
    @Schema(description = "Data id", example = "2a35bffe-27ad-4a50-a7e0-8c871cfd7cc5", maxLength = UUID_MAX_LENGTH)
    private String dataId;

    /**
     * Train data url in internal format data://dataId
     */
    @Schema(description = "Train data url in internal format data://dataId",
            example = "data://2a35bffe-27ad-4a50-a7e0-8c871cfd7cc5", maxLength = MAX_LENGTH_255)
    private String dataUrl;
}
