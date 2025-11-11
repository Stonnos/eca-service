package com.ecaservice.server.util;

import lombok.experimental.UtilityClass;

/**
 * S3 object paths.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class S3ObjectPaths {

    /**
     * Classifier model path format
     */
    public static final String CLASSIFIER_PATH_FORMAT = "classifiers/classifier-%s.model";

    /**
     * Experiment model path format
     */
    public static final String EXPERIMENT_PATH_FORMAT = "experiments/experiment-%s.zip";

    /**
     * Experiment results attachment path format
     */
    public static final String EXPERIMENT_RESULTS_ATTACHMENT_PATH_FORMAT =
            "experiments-details/%s/attachments/%s";

    /**
     * Classifier evaluation results attachment path format
     */
    public static final String EVALUATION_RESULTS_ATTACHMENT_PATH_FORMAT =
            "classifiers-details/%s/attachments/%s";

    /**
     * Classifier AUC predictions path format
     */
    public static final String CLASSIFIER_AUC_PREDICTIONS_PATH_FORMAT =
            "classifiers-details/%s/auc-predictions/classifier-auc-predictions-%s.model";

    /**
     * Classifier AUC predictions path format
     */
    public static final String EXPERIMENT_RESULTS_AUC_PREDICTIONS_PATH_FORMAT =
            "experiments-details/%s/auc-predictions/experiment-results-auc-predictions-%s-%d.model";
}
