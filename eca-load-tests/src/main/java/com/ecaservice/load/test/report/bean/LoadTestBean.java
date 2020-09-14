package com.ecaservice.load.test.report.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Load test bean.
 *
 * @author Roman Batygin
 */
@Data
public class LoadTestBean {

    /**
     * Load test uuid
     */
    private String testUuid;

    /**
     * Started date
     */
    private String started;

    /**
     * Finished date
     */
    private String finished;

    /**
     * Threads number for requests sending
     */
    private Integer numThreads;

    /**
     * Evaluation method
     */
    private String evaluationMethod;

    /**
     * Seed value for random generator
     */
    private Integer seed;

    /**
     * Test execution status
     */
    private String executionStatus;

    /**
     * Request total time
     */
    private String totalTime;

    /**
     * Total tests
     */
    private Integer total;

    /**
     * Passed tests count
     */
    private Integer passedCount;

    /**
     * Completed tests count
     */
    private Integer failedCount;

    /**
     * Error tests count
     */
    private Integer errorCount;

    /**
     * Tps value
     */
    private BigDecimal tps;

    /**
     * Evaluation tests list
     */
    private List<EvaluationTestBean> evaluationTests;
}
