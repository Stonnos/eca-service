package com.ecaservice.server.util;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.MessageError;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.server.model.entity.RequestStatusVisitor;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import eca.core.evaluation.EvaluationMethod;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ecaservice.server.util.ClassifierOptionsHelper.isParsableOptions;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String CV_FORMAT = "%d - блочная кросс - проверка";
    private static final String CV_EXTENDED_FORMAT = "%d×%d - блочная кросс - проверка";
    private static final long ZERO = 0L;

    /**
     * Creates error object.
     *
     * @param errorCode - error code
     * @return error object
     */
    public static MessageError error(ErrorCode errorCode) {
        MessageError error = new MessageError();
        error.setCode(errorCode.name());
        error.setMessage(errorCode.getErrorMessage());
        return error;
    }

    /**
     * Creates evaluation response with error status.
     *
     * @param errorCode - error code
     * @return evaluation response
     */
    public static EvaluationResponse buildEvaluationErrorResponse(ErrorCode errorCode) {
        EvaluationResponse evaluationResponse = new EvaluationResponse();
        evaluationResponse.setRequestId(UUID.randomUUID().toString());
        evaluationResponse.setStatus(TechnicalStatus.ERROR);
        MessageError error = error(errorCode);
        evaluationResponse.setErrors(Collections.singletonList(error));
        return evaluationResponse;
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
     * Finds first response model with valid classifier options.
     *
     * @param requestModel - classifier options request model
     * @return classifier options response model
     */
    public static ClassifierOptionsResponseModel getFirstResponseModel(ClassifierOptionsRequestModel requestModel) {
        if (Optional.ofNullable(requestModel).map(
                ClassifierOptionsRequestModel::getClassifierOptionsResponseModels).isEmpty()) {
            return null;
        } else {
            return requestModel.getClassifierOptionsResponseModels()
                    .stream()
                    .filter(responseModel -> isParsableOptions(responseModel.getOptions()))
                    .findFirst()
                    .orElse(null);
        }
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
     * Calculates request statuses statistics.
     *
     * @param requestStatusStatistics - request statuses statistics list
     * @return request status statistics dto
     */
    public static RequestStatusStatisticsDto calculateRequestStatusesStatistics(
            List<RequestStatusStatistics> requestStatusStatistics) {
        RequestStatusStatisticsDto requestStatusStatisticsDto = new RequestStatusStatisticsDto();
        requestStatusStatistics.forEach(item -> item.getRequestStatus().handle(
                new RequestStatusVisitor<Void, RequestStatusStatisticsDto>() {
                    @Override
                    public Void caseNew(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setNewRequestsCount(item.getRequestsCount());
                        return null;
                    }

                    @Override
                    public Void caseFinished(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setFinishedRequestsCount(item.getRequestsCount());
                        return null;
                    }

                    @Override
                    public Void caseTimeout(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setTimeoutRequestsCount(item.getRequestsCount());
                        return null;
                    }

                    @Override
                    public Void caseError(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setErrorRequestsCount(item.getRequestsCount());
                        return null;
                    }

                    @Override
                    public Void caseInProgress(RequestStatusStatisticsDto statisticsDto) {
                        statisticsDto.setInProgressRequestsCount(item.getRequestsCount());
                        return null;
                    }
                }, requestStatusStatisticsDto));
        long total = requestStatusStatistics.stream().mapToLong(RequestStatusStatistics::getRequestsCount).sum();
        requestStatusStatisticsDto.setTotalCount(total);
        return requestStatusStatisticsDto;
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
     * Creates classifier options request entity.
     *
     * @param source - classifier options request source
     * @return classifier options request entity
     */
    public static ClassifierOptionsRequestEntity createClassifierOptionsRequestEntity(
            ClassifierOptionsRequestSource source) {
        ClassifierOptionsRequestEntity requestEntity = new ClassifierOptionsRequestEntity();
        requestEntity.setCreationDate(LocalDateTime.now());
        requestEntity.setSource(source);
        return requestEntity;
    }
}
