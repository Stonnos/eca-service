package com.ecaservice.config.rabbit;

import lombok.experimental.UtilityClass;

/**
 * Rabbit MQ queues names.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Queues {

    /**
     * Evaluation request queue name
     */
    public static final String EVALUATION_REQUEST_QUEUE = "evaluation-request-queue";

    /**
     * Evaluation optimizer request queue name
     */
    public static final String EVALUATION_OPTIMIZER_REQUEST_QUEUE = "evaluation-optimizer-request-queue";

    /**
     * Experiment request queue name
     */
    public static final String EXPERIMENT_REQUEST_QUEUE = "experiment-request-queue";
}
