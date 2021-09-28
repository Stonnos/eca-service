package com.ecaservice.ers.util;

import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    /**
     * Creates evaluation results response.
     *
     * @param requestId      - request id
     * @return evaluation results response
     */
    public static EvaluationResultsResponse buildResponse(String requestId) {
        EvaluationResultsResponse evaluationResultsResponse = new EvaluationResultsResponse();
        evaluationResultsResponse.setRequestId(requestId);
        return evaluationResultsResponse;
    }

    /**
     * Creates classifier options response.
     *
     * @param requestId      - request id
     * @return classifier options response
     */
    public static ClassifierOptionsResponse buildClassifierOptionsResponse(String requestId) {
        ClassifierOptionsResponse classifierOptionsResponse = new ClassifierOptionsResponse();
        classifierOptionsResponse.setRequestId(requestId);
        return classifierOptionsResponse;
    }

    /**
     * Creates classifier options response.
     *
     * @param requestId         - request id
     * @param classifierReports - classifier reports
     * @return classifier options response
     */
    public static ClassifierOptionsResponse buildClassifierOptionsResponse(String requestId,
                                                                           List<ClassifierReport> classifierReports) {
        ClassifierOptionsResponse classifierOptionsResponse = buildClassifierOptionsResponse(requestId);
        classifierOptionsResponse.setClassifierReports(classifierReports);
        return classifierOptionsResponse;
    }

    /**
     * Returns integer value if specified big integer value is not null, null otherwise.
     *
     * @param value - big integer value
     * @return integer value
     */
    public static Integer toInteger(BigInteger value) {
        return Optional.ofNullable(value).map(BigInteger::intValue).orElse(null);
    }
}
