package com.ecaservice.data.storage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import static com.ecaservice.data.storage.dto.FieldConstraints.UUID_MAX_SIZE;

/**
 * Export instances response dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Export instances response dto")
public class ExportInstancesResponseDto {

    @Tolerate
    public ExportInstancesResponseDto() {
        //default constructor
    }

    /**
     * External instances uuid in central data storage
     */
    @Schema(description = "External instances uuid in central data storage",
            example = "f8cecbf7-405b-403b-9a94-f51e8fb73ed8", maxLength = UUID_MAX_SIZE)
    private String externalDataUuid;
}
