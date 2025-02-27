package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Classify instance result dto.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Classify instance result model")
public class ClassifyInstanceResultDto {

    /**
     * Class index
     */
    @Schema(description = "Class index", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer classIndex;

    /**
     * Class value
     */
    @Schema(description = "Class value", example = "classValue", requiredMode = Schema.RequiredMode.REQUIRED)
    private String classValue;

    /**
     * Class value probability
     */
    @Schema(description = "True positive rate", example = "0.75", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal probability;
}
