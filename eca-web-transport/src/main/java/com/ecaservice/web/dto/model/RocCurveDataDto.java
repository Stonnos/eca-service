package com.ecaservice.web.dto.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Roc - curve data dto.
 *
 * @author Roman Batygin
 */
@Data
@ApiModel(description = "Roc - curve data model")
public class RocCurveDataDto {

    /**
     * Class value
     */
    @ApiModelProperty(value = "Class value")
    private String classValue;

    /**
     * Roc - curve points
     */
    @ApiModelProperty(value = "Roc - curve points")
    private List<RocCurvePointDto> points;
}
