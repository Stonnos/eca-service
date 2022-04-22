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
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.server.model.projections.RequestStatusStatistics;
import com.ecaservice.web.dto.model.EnumDto;
import com.ecaservice.web.dto.model.EvaluationResultsDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import eca.core.evaluation.EvaluationMethod;
import eca.text.NumericFormatFactory;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecaservice.server.util.ClassifierOptionsHelper.isParsableOptions;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final DecimalFormat DECIMAL_FORMAT = NumericFormatFactory.getInstance(Integer.MAX_VALUE);

    private static final String DOWNLOAD_PATH_FORMAT = "%s/eca-api/experiment/download/%s";
    private static final String ATTACHMENT = "attachment";
    private static final String CV_FORMAT = "%d - блочная кросс - проверка";
    private static final String CV_EXTENDED_FORMAT = "%d×%d - блочная кросс - проверка";
    private static final long ZERO = 0L;

    /**
     * Builds experiment download url.
     *
     * @param baseUrl - base url
     * @param token   - experiment token
     * @return experiment download url
     */
    public static String buildExperimentDownloadUrl(String baseUrl, String token) {
        return String.format(DOWNLOAD_PATH_FORMAT, baseUrl, token);
    }

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
     * Creates response with attachment.
     *
     * @param file - file attachment
     * @return response entity
     */
    public static ResponseEntity<FileSystemResource> buildAttachmentResponse(File file) {
        FileSystemResource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, resource.getFilename());
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    /**
     * Checks file existing.
     *
     * @param file - file
     * @return {@code true} if file is existing
     */
    public static boolean existsFile(File file) {
        return file != null && file.isFile();
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
     * Transform requests statuses map to statistics dto.
     *
     * @param statusStatisticsMap - requests statuses map
     * @return request status statistics dto
     */
    public static RequestStatusStatisticsDto toRequestStatusesStatistics(Map<RequestStatus, Long> statusStatisticsMap) {
        RequestStatusStatisticsDto requestStatusStatisticsDto = new RequestStatusStatisticsDto();
        requestStatusStatisticsDto.setNewRequestsCount(statusStatisticsMap.getOrDefault(RequestStatus.NEW, ZERO));
        requestStatusStatisticsDto.setInProgressRequestsCount(
                statusStatisticsMap.getOrDefault(RequestStatus.IN_PROGRESS, ZERO));
        requestStatusStatisticsDto.setFinishedRequestsCount(
                statusStatisticsMap.getOrDefault(RequestStatus.FINISHED, ZERO));
        requestStatusStatisticsDto.setTimeoutRequestsCount(
                statusStatisticsMap.getOrDefault(RequestStatus.TIMEOUT, ZERO));
        requestStatusStatisticsDto.setErrorRequestsCount(statusStatisticsMap.getOrDefault(RequestStatus.ERROR, ZERO));
        requestStatusStatisticsDto.setTotalCount(
                statusStatisticsMap.values().stream().mapToLong(Long::longValue).sum());
        return requestStatusStatisticsDto;
    }

    /**
     * Transforms requests statuses list to map.
     *
     * @param requestStatusStatistics - request statuses list
     * @return request statuses map
     */
    public static Map<RequestStatus, Long> toRequestStatusStatisticsMap(
            List<RequestStatusStatistics> requestStatusStatistics) {
        if (CollectionUtils.isEmpty(requestStatusStatistics)) {
            return Collections.emptyMap();
        }
        Map<RequestStatus, Long> requestStatusMap = requestStatusStatistics
                .stream()
                .collect(Collectors.toMap(RequestStatusStatistics::getRequestStatus,
                        RequestStatusStatistics::getRequestsCount, (v1, v2) -> v1, TreeMap::new));
        Arrays.stream(RequestStatus.values()).filter(
                requestStatus -> !requestStatusMap.containsKey(requestStatus)).forEach(
                requestStatus -> requestStatusMap.put(requestStatus, ZERO));
        return requestStatusMap;
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

    /**
     * Formats object value to string.
     *
     * @param value - value
     * @return string value
     */
    public static String formatValue(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        String strValue = String.valueOf(value);
        if (NumberUtils.isParsable(strValue)) {
            return DECIMAL_FORMAT.format(strValue);
        }
        return strValue;
    }
}
