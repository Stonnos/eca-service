package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Filter request model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Filter request model")
public class FilterRequestDto {

    /**
     * Column name
     */
    @NotBlank
    @ApiModelProperty(value = "Filter column name")
    private String name;

    /**
     * Column values to filter
     */
    @ApiModelProperty(value = "Column values to filter")
    private List<String> values;

    /**
     * Match mode type {@link MatchMode}
     */
    @NotNull
    @ApiModelProperty(value = "Match mode type")
    private MatchMode matchMode;
}
