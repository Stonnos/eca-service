package com.ecaservice.server.model.evaluation;

import lombok.Data;

import java.util.List;

/**
 * Confusion matrix data.
 *
 * @author Roman Batygin
 */
@Data
public class ConfusionMatrixData {

    /**
     * Class values list
     */
    public List<String> classValues;

    /**
     * Confusion matrix cells
     */
    public List<List<ConfusionMatrixCellData>> confusionMatrixCells;
}
