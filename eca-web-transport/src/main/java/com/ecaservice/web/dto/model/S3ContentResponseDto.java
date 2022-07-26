package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_1024;

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
     * Content url.
     */
    @Schema(description = "Content url", maxLength = MAX_LENGTH_1024)
    private String contentUrl;
}
