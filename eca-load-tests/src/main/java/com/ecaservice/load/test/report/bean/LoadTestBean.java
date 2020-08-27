package com.ecaservice.load.test.report.bean;

import com.ecaservice.load.test.entity.ExecutionStatus;
import lombok.Data;

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
     * Requests number to eca - server
     */
    private Integer numRequests;

    /**
     * Threads number for requests sending
     */
    private Integer numThreads;

    /**
     * Evaluation method
     */
    private String evaluationMethod;

    /**
     * Test execution status
     */
    private ExecutionStatus executionStatus;

    /**
     * Evaluation tests list
     */
    private List<EvaluationTestBean> evaluationTests;
}
