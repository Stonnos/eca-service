package com.ecaservice.auto.test.model.evaluation;

import com.ecaservice.test.common.model.MatchResult;
import lombok.Data;

import java.math.BigInteger;

/**
 * Confusion matrix details match.
 *
 * @author Roman Batygin
 */
@Data
public class ConfusionMatrixDetailsMatch {

    /**
     * Expected actual class value
     */
    private String expectedActualClassValue;

    /**
     * Actual class value
     */
    private String actualClassValue;

    /**
     * Actual class value match result
     */
    private MatchResult actualClassValueMatchResult;

    /**
     * Expected predicted class value
     */
    private String expectedPredictedClassValue;

    /**
     * Actual predicted class value
     */
    private String actualPredictedClassValue;

    /**
     * Predicted class value match result
     */
    private MatchResult predictedClassValueMatchResult;

    /**
     * Expected instances number
     */
    private BigInteger expectedNumInstances;

    /**
     * Actual instances number
     */
    private BigInteger actualNumInstances;

    /**
     * Instances number match result
     */
    private MatchResult numInstancesMatchResult;
}
