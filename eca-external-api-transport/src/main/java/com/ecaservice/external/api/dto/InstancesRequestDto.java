package com.ecaservice.external.api.dto;

import com.ecaservice.external.api.dto.annotations.DataURL;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Instances request dto model.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Instances request model")
public class InstancesRequestDto implements Serializable {

    /**
     * Training data url
     */
    @DataURL
    @Schema(description = "Train data url", example = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls",
            required = true)
    private String trainDataUrl;
}
