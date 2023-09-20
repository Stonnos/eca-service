package com.ecaservice.web.dto.model;

import com.ecaservice.common.error.model.ValidationErrorDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * Password validation error dto.
 *
 * @author Roman Batygin
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Password validation error model")
public class PasswordValidationErrorDto extends ValidationErrorDto {

    /**
     * Password validation result details
     */
    @Schema(description = "Password validation result details")
    private List<PasswordRuleResultDto> details;
}
