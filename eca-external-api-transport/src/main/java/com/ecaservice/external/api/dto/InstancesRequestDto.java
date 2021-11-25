package com.ecaservice.external.api.dto;

import com.ecaservice.external.api.dto.annotations.DataURL;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;
import java.io.Serializable;

import static com.ecaservice.external.api.dto.Constraints.MAX_LENGTH_255;
import static com.ecaservice.external.api.dto.Constraints.MIN_LENGTH_1;

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
    @Size(min = MIN_LENGTH_1, max = MAX_LENGTH_255)
    @Schema(description = "Train data url", example = "http://kt.ijs.si/Branax/Repository/WEKA/Iris.xls",
            required = true)
    private String trainDataUrl;
}
