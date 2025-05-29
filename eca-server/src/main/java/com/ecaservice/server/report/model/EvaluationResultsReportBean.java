package com.ecaservice.server.report.model;

import com.ecaservice.server.model.evaluation.ConfusionMatrixCellData;
import com.ecaservice.web.dto.model.ClassifierInfoDto;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Evaluation results report bean.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationResultsReportBean {

    /**
     * Request unique identifier
     */
    private String requestId;

    /**
     * Classifier info
     */
    private ClassifierInfoDto classifierInfo;

    /**
     * Training data name
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
     * Classes number
     */
    private Integer numClasses;

    /**
     * Class name
     */
    private String className;

    /**
     * Evaluation method
     */
    private String evaluationMethod;

    /**
     * Test instances number
     */
    private BigInteger numTestInstances;

    /**
     * Correctly classified instances number
     */
    private BigInteger numCorrect;

    /**
     * Incorrectly classified instances number
     */
    private BigInteger numIncorrect;

    /**
     * Correctly classified percentage
     */
    private BigDecimal pctCorrect;

    /**
     * Incorrectly classified percentage
     */
    private BigDecimal pctIncorrect;

    /**
     * Mean absolute error
     */
    private BigDecimal meanAbsoluteError;

    /**
     * Root mean squared error
     */
    private BigDecimal rootMeanSquaredError;

    /**
     * Variance error
     */
    private BigDecimal varianceError;

    /**
     * 95% confidence interval lower bound value
     */
    private BigDecimal confidenceIntervalLowerBound;

    /**
     * 95% confidence interval upper bound value
     */
    private BigDecimal confidenceIntervalUpperBound;

    /**
     * Classification costs report
     */
    private List<ClassificationCostsReportBean> classificationCosts;

    /**
     * Class values
     */
    private List<String> classValues;

    /**
     * Confusion matrix cells
     */
    private List<List<ConfusionMatrixCellData>> confusionMatrixCells;
}
