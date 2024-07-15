package com.ecaservice.web.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Confusion matrix dto.
 *
 * @author Roman Batygin
 */
@Data
@Schema(description = "Confusion matrix model")
public class ConfusionMatrixDto {

    /**
     * Class values list
     */
    @Schema(description = "Class values")
    public List<String> classValues;

    /**
     * Confusion matrix cells
     */
    @Schema(description = "Confusion matrix cells")
    public List<List<ConfusionMatrixCellDto>> confusionMatrixCells;
}
