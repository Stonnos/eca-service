package com.ecaservice.server.model.evaluation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.classifiers.evaluation.Prediction;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * AUC predictions data.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AucPredictionsData implements Serializable {

    /**
     * Predictions list for computed roc curve data
     */
    private ArrayList<Prediction> predictions;
}
