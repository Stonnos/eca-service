package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * Create classifiers configuration dto.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Create classifiers configuration model")
public class CreateClassifiersConfigurationDto {

    /**
     * Configuration name
     */
    @NotBlank
    @ApiModelProperty(value = "Configuration name", required = true)
    private String name;
}