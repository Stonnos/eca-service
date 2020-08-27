package com.ecaservice.load.test.report.bean;

import com.ecaservice.load.test.entity.RequestStageType;
import com.ecaservice.load.test.entity.TestResult;
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
    private RequestStageType stageType;

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
    private TestResult testResult;

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