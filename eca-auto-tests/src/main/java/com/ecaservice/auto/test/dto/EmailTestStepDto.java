package com.ecaservice.auto.test.dto;

import com.ecaservice.auto.test.model.EmailType;
import com.ecaservice.test.common.model.MatchResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Email test step dto.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmailTestStepDto extends BaseTestStepDto {

    /**
     * Email type
     */
    @Schema(description = "Email type")
    private EmailType emailType;

    /**
     * Email type description
     */
    @Schema(description = "Email type description")
    private String emailTypeDescription;

    /**
     * Message received?
     */
    @Schema(description = "Message received?")
    private boolean messageReceived;

    /**
     * Expected download url
     */
    @Schema(description = "Expected download url")
    private String expectedDownloadUrl;

    /**
     * Actual download url
     */
    @Schema(description = "Actual download url")
    private String actualDownloadUrl;

    /**
     * Download url match result
     */
    @Schema(description = "Download url match result")
    private MatchResult downloadUrlMatchResult;
}
