package com.ecaservice.util;

import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.exception.EcaServiceException;
import com.ecaservice.model.InputData;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.evaluation.EvaluationMethod;
import com.ecaservice.model.evaluation.EvaluationOption;
import org.springframework.util.Assert;
import weka.core.Instances;
import weka.core.xml.XMLInstances;

import java.util.Map;
import java.util.UUID;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
public class Utils {

    /**
     * Validates classifier input data.
     *
     * @param inputData            - input data
     * @param evaluationMethod     - evaluation method
     * @param evaluationOptionsMap - evaluation options map
     */
    public static void validateInputData(InputData inputData, EvaluationMethod evaluationMethod,
                                         Map<EvaluationOption, String> evaluationOptionsMap) {
        Assert.notNull(inputData, "Input data is not specified!");
        Assert.notNull(inputData.getClassifier(), "Classifier is not specified!");
        Assert.notNull(inputData.getData(), "Input data is not specified!");
        Assert.notNull(evaluationMethod, "Evaluation method is not specified!");
        Assert.notNull(evaluationOptionsMap, "Evaluation options map is not specified!");
    }

    /**
     * Creates evaluation response with error status.
     *
     * @param errorMessage - error message
     * @return evaluation response
     */
    public static EvaluationResponse buildErrorResponse(String errorMessage) {
        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setRequestId(UUID.randomUUID().toString());
        evaluationResponse.setStatus(TechnicalStatus.ERROR);
        evaluationResponse.setErrorMessage(errorMessage);
        return evaluationResponse;
    }

    /**
     * Transforms instances to xml string.
     *
     * @param data - training data
     * @return data as xml
     */
    public static String toXmlInstances(Instances data) {
        try {
            XMLInstances xmlInstances = new XMLInstances();
            xmlInstances.setInstances(data);
            return xmlInstances.toString();
        } catch (Exception ex) {
            throw new EcaServiceException(ex.getMessage());
        }
    }
}
