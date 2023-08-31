package com.ecaservice.data.loader.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import static com.ecaservice.data.loader.dto.FieldConstraints.UUID_MAX_SIZE;

/**
 * Upload instances response dto.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "Upload instances response dto")
public class UploadInstancesResponseDto {

    @Tolerate
    public UploadInstancesResponseDto() {
        //default constructor
    }

    /**
     * Instances uuid
     */
    @Schema(description = "Instances uuid", example = "f8cecbf7-405b-403b-9a94-f51e8fb73ed8", maxLength = UUID_MAX_SIZE)
    private String uuid;
}
