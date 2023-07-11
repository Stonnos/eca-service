package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.util.FieldConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1;

/**
 * Update classifiers configuration dto.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Update classifiers configuration model")
public class UpdateClassifiersConfigurationDto {

    /**
     * Configuration id
     */
    @NotNull
    @Min(VALUE_1)
    @Max(Long.MAX_VALUE)
    @Schema(description = "Configuration id", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    /**
     * Configuration name
     */
    @NotBlank
    @Size(min = VALUE_1, max = FieldConstraints.CONFIGURATION_NAME_MAX_LENGTH)
    @Schema(description = "Configuration name", example = "Classifiers configuration",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String configurationName;
}
