package com.ecaservice.server.util;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.base.model.MessageError;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.core.model.ClassificationModel;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import weka.classifiers.AbstractClassifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String CV_FORMAT = "%d - блочная кросс - проверка";
    private static final String CV_EXTENDED_FORMAT = "%d×%d - блочная кросс - проверка";

    /**
     * Creates error object.
     *
     * @param errorCode - error code
     * @return error object
     */
    public static MessageError error(ErrorCode errorCode) {
        MessageError error = new MessageError();
        error.setCode(errorCode);
        error.setMessage(errorCode.getErrorMessage());
        return error;
    }

    /**
     * Creates response with error status.
     *
     * @param errorCode - error code
     * @return eca response
     */
    public static EcaResponse buildErrorResponse(ErrorCode errorCode) {
        EcaResponse ecaResponse = new EcaResponse();
        ecaResponse.setRequestId(UUID.randomUUID().toString());
        ecaResponse.setStatus(TechnicalStatus.ERROR);
        MessageError error = error(errorCode);
        ecaResponse.setErrors(Collections.singletonList(error));
        return ecaResponse;
    }

    /**
     * Creates response with validation error status.
     *
     * @param errors - error message
     * @return evaluation response
     */
    public static EcaResponse buildValidationError(List<MessageError> errors) {
        EcaResponse ecaResponse = new EcaResponse();
        ecaResponse.setRequestId(UUID.randomUUID().toString());
        ecaResponse.setStatus(TechnicalStatus.VALIDATION_ERROR);
        ecaResponse.setErrors(errors);
        return ecaResponse;
    }

    /**
     * Build evaluation response data model.
     *
     * @param requestId - request id
     * @param status    - request status
     * @return evaluation response data model
     */
    public static EvaluationResultsDataModel buildEvaluationResultsModel(String requestId, RequestStatus status) {
        EvaluationResultsDataModel evaluationResultsDataModel = new EvaluationResultsDataModel();
        evaluationResultsDataModel.setRequestId(requestId);
        evaluationResultsDataModel.setStatus(status);
        return evaluationResultsDataModel;
    }

    /**
     * Build evaluation data model error response.
     *
     * @param requestId - request id
     * @param errorCode - error code
     * @return evaluation model data response
     */
    public static EvaluationResultsDataModel buildErrorEvaluationResultsModel(String requestId,
                                                                              ErrorCode errorCode) {
        EvaluationResultsDataModel evaluationResultsDataModel =
                buildEvaluationResultsModel(requestId, RequestStatus.ERROR);
        evaluationResultsDataModel.setErrorCode(errorCode);
        return evaluationResultsDataModel;
    }

    /**
     * Build evaluation data model internal error response.
     *
     * @param requestId - request id
     * @return evaluation model data response
     */
    public static EvaluationResultsDataModel buildInternalErrorEvaluationResultsModel(String requestId) {
        return buildErrorEvaluationResultsModel(requestId, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * Checks classifier report for empty options.
     *
     * @param classifierReport - classifier report
     * @return {@code true} if classifier options is not null or empty
     */
    public static boolean isValid(ClassifierReport classifierReport) {
        return classifierReport != null && !StringUtils.isEmpty(classifierReport.getOptions());
    }

    /**
     * Gets first classifier report.
     *
     * @param classifierOptionsResponse - classifier options response
     * @return classifier report
     */
    public static ClassifierReport getFirstClassifierReport(ClassifierOptionsResponse classifierOptionsResponse) {
        if (classifierOptionsResponse == null ||
                CollectionUtils.isEmpty(classifierOptionsResponse.getClassifierReports())) {
            return null;
        }
        return classifierOptionsResponse.getClassifierReports()
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets start time of specified local date.
     *
     * @param localDate - local date
     * @return local date time
     */
    public static LocalDateTime atStartOfDay(LocalDate localDate) {
        return localDate != null ? localDate.atStartOfDay() : null;
    }

    /**
     * Gets end time of specified local date.
     *
     * @param localDate - local date
     * @return local date time
     */
    public static LocalDateTime atEndOfDay(LocalDate localDate) {
        return localDate != null ? localDate.atTime(LocalTime.MAX) : null;
    }

    /**
     * Creates evaluation results dto with specified status.
     *
     * @param evaluationResultsStatus - evaluation results status
     * @return evaluation results dto
     */
    public static EvaluationResultsDto buildEvaluationResultsDto(EvaluationResultsStatus evaluationResultsStatus) {
        EvaluationResultsDto evaluationResultsDto = new EvaluationResultsDto();
        evaluationResultsDto.setEvaluationResultsStatus(
                new EnumDto(evaluationResultsStatus.name(), evaluationResultsStatus.getDescription()));
        return evaluationResultsDto;
    }

    /**
     * Gets evaluation method description.
     *
     * @param evaluationMethod - evaluation method
     * @param numFolds         - num folds for k * V cross - validation method
     * @param numTests         - num tests for k * V cross - validation method
     * @return evaluation method description
     */
    public static String getEvaluationMethodDescription(EvaluationMethod evaluationMethod, Integer numFolds,
                                                        Integer numTests) {
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            return numTests == 1 ? String.format(CV_FORMAT, numFolds) :
                    String.format(CV_EXTENDED_FORMAT, numFolds, numTests);
        }
        return evaluationMethod.getDescription();
    }

    /**
     * Builds classification model from evaluation results.
     *
     * @param evaluationResults - evaluation results
     * @return classification model
     */
    public static ClassificationModel buildClassificationModel(EvaluationResults evaluationResults) {
        var classifier = (AbstractClassifier) evaluationResults.getClassifier();
        ClassificationModel classificationModel = new ClassificationModel();
        classificationModel.setClassifier(classifier);
        classificationModel.setData(evaluationResults.getEvaluation().getData());
        classificationModel.setEvaluation(evaluationResults.getEvaluation());
        return classificationModel;
    }
}
