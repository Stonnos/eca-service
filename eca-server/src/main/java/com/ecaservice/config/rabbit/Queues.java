package com.ecaservice.config.rabbit;

/**
 * Rabbit MQ queues names.
 *
 * @author Roman Batygin
 */
public class Queues {

    /**
     * Evaluation request queue name
     */
    public static final String EVALUATION_REQUEST_QUEUE = "evaluation-request-queue";

    /**
     * Evaluation response queue name
     */
    public static final String EVALUATION_RESULTS_QUEUE = "evaluation-results-queue";

    private Queues() {
    }
}
