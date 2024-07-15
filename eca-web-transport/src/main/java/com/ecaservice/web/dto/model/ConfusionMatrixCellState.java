package com.ecaservice.web.dto.model;

/**
 * Confusion matrix cell state.
 *
 * @author Roman Batygin
 */
public enum ConfusionMatrixCellState {

    /**
     * Green state, mean that all predicted and actual instances matches
     */

    GREEN,

    /**
     * Yellow state, mean that some predicted and actual instances not matches
     */
    YELLOW,

    /**
     * White state used if confusion matrix cell value is zero
     */
    WHITE
}
