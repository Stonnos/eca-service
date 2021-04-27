package com.ecaservice.ers.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static com.ecaservice.ers.dto.Constraints.MAX_LENGTH_255;

/**
 * Classifier input option model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Classifier input option model")
public class ClassifierInputOption {

    /**
     * Option key
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Option key", required = true)
    private String key;

    /**
     * Option value
     */
    @NotBlank
    @Size(max = MAX_LENGTH_255)
    @ApiModelProperty(value = "Option value", required = true)
    private String value;
}
