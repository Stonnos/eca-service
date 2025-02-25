package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import static com.ecaservice.web.dto.util.FieldConstraints.VALUE_1_STRING;
import static com.ecaservice.web.dto.util.FieldConstraints.ZERO_VALUE_STRING;

/**
 * Roc curve data dto.
 *
 * @author Roman Batygin
 */
@Data
public class RocCurveDataDto {

    /**
     * Roc curve data points
     */
    @Schema(description = "Roc curve data points")
    private List<RocCurvePointDto> rocCurvePoints;

    /**
     * AUC value
     */
    @Schema(description = "AUC value", example = "0.97", minimum = ZERO_VALUE_STRING, maximum = VALUE_1_STRING)
    private BigDecimal aucValue;
}
