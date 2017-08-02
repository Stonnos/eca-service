package com.ecaservice.service;

import com.ecaservice.model.ClassificationResult;
import com.ecaservice.model.EvaluationMethod;

import java.time.LocalDateTime;

/**
 * Service for saving evaluation request params into database.
 * @author Roman Batygin
 */
public interface EvaluationLogService {

    /**
     * Saves evaluation request params into database.
     * @param result <tt>ClassificationResult</tt> object
     * @param evaluationMethod evaluation method
     * @param numFolds the number of folds for k * V cross - validation method
     * @param numTests the number of tests for k * V cross - validation method
     * @param requestDate request date
     * @param ipAddress client ip address
     */
    void save(ClassificationResult result, EvaluationMethod evaluationMethod,
              Integer numFolds, Integer numTests,
              LocalDateTime requestDate, String ipAddress);
}
