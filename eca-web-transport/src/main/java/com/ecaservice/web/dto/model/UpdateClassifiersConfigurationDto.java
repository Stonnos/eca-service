package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Update classifiers configuration dto.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Update experiment result model")
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
    @ApiModelProperty(value = "Configuration name", required = true)
    private String name;
}