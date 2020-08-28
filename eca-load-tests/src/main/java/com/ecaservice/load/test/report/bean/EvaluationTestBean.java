package com.ecaservice.load.test.report.bean;

import lombok.Data;

/**
 * Evaluation test bean.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationTestBean {

    /**
     * Message correlation id
     */
    private String correlationId;

    /**
     * Request stage
     */
    private String stageType;

    /**
     * Started date
     */
    private String started;

    /**
     * Finished date
     */
    private String finished;

    /**
     * Test result
     */
    private String testResult;

    /**
     * Classifier name
     */
    private String classifierName;

    /**
     * Classifier options json config
     */
    private String classifierOptions;

    /**
     * Instances name
     */
    private String relationName;

    /**
     * Instances number
     */
    private Integer numInstances;

    /**
     * Attributes number
     */
    private Integer numAttributes;

    /**
     * Request total time
     */
    private String totalTime;
}