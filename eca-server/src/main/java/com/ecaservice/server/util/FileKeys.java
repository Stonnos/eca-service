package com.ecaservice.server.util;

import lombok.experimental.UtilityClass;

/**
 * File keys utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FileKeys {

    /**
     * Experiment results AUC predictions
     */
    public static final String EXPERIMENT_RESULTS_AUC_PREDICTIONS = "experiment-results-auc-predictions-%s-%d.model";

    /**
     * Experiment model zip.
     */
    public static final String EXPERIMENT_MODEL_ZIP = "experiment-%s.zip";
}
