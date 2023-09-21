package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.ecaservice.common.error.model.Constraints.STRING_MAX_LENGTH_255;

/**
 * Password rule result dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Password rule result dto")
public class PasswordRuleResultDto {

    /**
     * Rule name
     */
    @Schema(description = "Rule name", example = "MIN_LENGTH", maxLength = STRING_MAX_LENGTH_255)
    private String rule;

    /**
     * Is valid?
     */
    @Schema(description = "Is valid?")
    private boolean valid;

    /**
     * Message
     */
    @Schema(description = "Message", maxLength = STRING_MAX_LENGTH_255)
    private String message;
}
