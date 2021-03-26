package com.ecaservice.ers.util;

import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.dto.ResponseStatus;
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
     * @param responseStatus - response status
     * @return evaluation results response
     */
    public static EvaluationResultsResponse buildResponse(String requestId, ResponseStatus responseStatus) {
        EvaluationResultsResponse evaluationResultsResponse = new EvaluationResultsResponse();
        evaluationResultsResponse.setRequestId(requestId);
        evaluationResultsResponse.setStatus(responseStatus);
        return evaluationResultsResponse;
    }

    /**
     * Creates classifier options response.
     *
     * @param requestId      - request id
     * @param responseStatus - response status
     * @return classifier options response
     */
    public static ClassifierOptionsResponse buildClassifierOptionsResponse(String requestId,
                                                                           ResponseStatus responseStatus) {
        ClassifierOptionsResponse classifierOptionsResponse = new ClassifierOptionsResponse();
        classifierOptionsResponse.setRequestId(requestId);
        classifierOptionsResponse.setStatus(responseStatus);
        return classifierOptionsResponse;
    }

    /**
     * Creates evaluation results response.
     *
     * @param requestId      - request id
     * @param responseStatus - response status
     * @return evaluation results simple response
     */
    public static GetEvaluationResultsResponse buildEvaluationResultsResponse(String requestId,
                                                                              ResponseStatus responseStatus) {
        GetEvaluationResultsResponse evaluationResultsResponse = new GetEvaluationResultsResponse();
        evaluationResultsResponse.setRequestId(requestId);
        evaluationResultsResponse.setStatus(responseStatus);
        return evaluationResultsResponse;
    }

    /**
     * Creates classifier options response.
     *
     * @param requestId         - request id
     * @param classifierReports - classifier reports
     * @param responseStatus    - response status
     * @return classifier options response
     */
    public static ClassifierOptionsResponse buildClassifierOptionsResponse(String requestId,
                                                                           List<ClassifierReport> classifierReports,
                                                                           ResponseStatus responseStatus) {
        ClassifierOptionsResponse classifierOptionsResponse = buildClassifierOptionsResponse(requestId, responseStatus);
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
