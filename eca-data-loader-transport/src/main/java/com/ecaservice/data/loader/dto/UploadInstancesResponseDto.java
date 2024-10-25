package com.ecaservice.data.loader.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.data.loader.dto.FieldConstraints.UUID_MAX_SIZE;

/**
 * Upload instances response dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Upload instances response dto")
public class UploadInstancesResponseDto {

    /**
     * Instances uuid
     */
    @Schema(description = "Instances uuid", example = "f8cecbf7-405b-403b-9a94-f51e8fb73ed8", maxLength = UUID_MAX_SIZE)
    private String uuid;

    /**
     * Instances file MD5 hash sum
     */
    @Schema(description = "Instances file MD5 hash sum", example = "3032e188204cb537f69fc7364f638641",
            maxLength = FieldConstraints.MAX_LENGTH_255)
    private String md5Hash;
}
