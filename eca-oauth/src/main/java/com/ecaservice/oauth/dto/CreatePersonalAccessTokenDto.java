package com.ecaservice.oauth.dto;

import com.ecaservice.user.dto.PersonalAccessTokenType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static com.ecaservice.web.dto.util.FieldConstraints.MAX_LENGTH_255;
import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Create personal access token dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Create personal access token dto")
public class CreatePersonalAccessTokenDto {

    /**
     * Token unique name
     */
    @NotBlank
    @Size(min = VALUE_1, max = MAX_LENGTH_255)
    @Schema(description = "Token unique name", maxLength = MAX_LENGTH_255, example = "Token name")
    private String name;

    /**
     * Token type
     */
    @NotNull
    @Schema(description = "Token type", requiredMode = Schema.RequiredMode.REQUIRED, maxLength = MAX_LENGTH_255)
    private PersonalAccessTokenType tokenType;

    /**
     * Expiration month
     */
    @NotNull
    @Min(VALUE_1)
    @Max(Integer.MAX_VALUE)
    @Schema(description = "Expiration month", example = "3")
    private Integer expirationMonth;
}
