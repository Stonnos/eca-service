package com.ecaservice.server.model.evaluation;

import com.ecaservice.web.dto.model.ConfusionMatrixCellState;
import lombok.Data;

/**
 * Confusion matrix cell data.
 *
 * @author Roman Batygin
 */
@Data
public class ConfusionMatrixCellData {

    /**
     * Instances number
     */
    private int numInstances;

    /**
     * Cell state
     */
    private ConfusionMatrixCellState state;
}
