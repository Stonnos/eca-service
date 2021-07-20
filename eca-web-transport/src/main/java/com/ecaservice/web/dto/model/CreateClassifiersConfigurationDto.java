package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.util.FieldConstraints;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Create classifiers configuration dto.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Create classifiers configuration model")
public class CreateClassifiersConfigurationDto {

    /**
     * Configuration name
     */
    @NotBlank
    @Size(max = FieldConstraints.CONFIGURATION_NAME_MAX_LENGTH)
    @Schema(description = "Configuration name", example = "Classifiers configuration", required = true)
    private String configurationName;
}
