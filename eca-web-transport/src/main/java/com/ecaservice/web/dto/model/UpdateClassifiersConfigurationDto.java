package com.ecaservice.web.dto.model;

import com.ecaservice.web.dto.util.FieldConstraints;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Update classifiers configuration dto.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Update classifiers configuration model")
public class UpdateClassifiersConfigurationDto {

    /**
     * Configuration id
     */
    @NotNull
    @ApiModelProperty(value = "Configuration id", required = true)
    private Long id;

    /**
     * Configuration name
     */
    @NotBlank
    @Size(max = FieldConstraints.CONFIGURATION_NAME_MAX_LENGTH)
    @ApiModelProperty(value = "Configuration name", required = true)
    private String configurationName;
}