package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_1024;
import static com.ecaservice.web.dto.util.FieldConstraints.UUID_MAX_LENGTH;

/**
 * S3 content response.
 *
 * @author Roman Batygin
 */
@Data
@Builder
@Schema(description = "S3 content response")
public class S3ContentResponseDto {

    /**
     * Content request id.
     */
    @Schema(description = "Content request id", example = "1d2de514-3a87-4620-9b97-c260e24340de",
            maxLength = UUID_MAX_LENGTH)
    private String requestId;

    /**
     * Content url.
     */
    @Schema(description = "Content url", maxLength = MAX_LENGTH_1024)
    private String contentUrl;
}
