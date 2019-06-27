package com.ecaservice.util;

import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.exception.EcaServiceException;
import com.ecaservice.model.InputData;
import com.ecaservice.model.TechnicalStatus;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.evaluation.EvaluationOption;
import com.ecaservice.model.options.ClassifierOptions;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationMethod;
import eca.data.file.xml.converter.XmlInstancesConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import weka.core.Instances;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
public class Utils {

    private static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String ATTACHMENT = "attachment";

    private static final String POINT_SEPARATOR = ".";

    private static ObjectMapper objectMapper = new ObjectMapper();

    private Utils() {
    }

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
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            Assert.notNull(evaluationOptionsMap, "Evaluation options map is not specified!");
        }
    }

    /**
     * Validate email.
     *
     * @param email - email
     * @return {@code true} is email valid
     */
    public static boolean isValidEmail(String email) {
        return !StringUtils.isEmpty(email) && email.matches(EMAIL_REGEX);
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
            XmlInstancesConverter xmlInstancesConverter = new XmlInstancesConverter();
            return xmlInstancesConverter.toXmlString(data);
        } catch (Exception ex) {
            throw new EcaServiceException(ex.getMessage());
        }
    }

    /**
     * Parses classifier options json string.
     *
     * @param options - classifier options json string
     * @return classifier options object
     */
    public static ClassifierOptions parseOptions(String options) {
        try {
            return objectMapper.readValue(options, ClassifierOptions.class);
        } catch (Exception ex) {
            throw new EcaServiceException(ex.getMessage());
        }
    }

    /**
     * Parses classifier options input stream.
     *
     * @param inputStream - classifier options as input stream
     * @return classifier options object
     */
    public static ClassifierOptions parseOptions(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, ClassifierOptions.class);
        } catch (Exception ex) {
            throw new EcaServiceException(ex.getMessage());
        }
    }

    /**
     * Checks classifier options json string deserialization.
     *
     * @param options - classifier options json string
     * @return {@code true} if classifier options json string can be deserialize
     */
    public static boolean isParsableOptions(String options) {
        if (StringUtils.isEmpty(options)) {
            return false;
        } else {
            try {
                parseOptions(options);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * Finds first response model with valid classifier options.
     *
     * @param requestModel - classifier options request model
     * @return classifier options response model
     */
    public static ClassifierOptionsResponseModel getFirstResponseModel(ClassifierOptionsRequestModel requestModel) {
        if (!Optional.ofNullable(requestModel).map(
                ClassifierOptionsRequestModel::getClassifierOptionsResponseModels).isPresent()) {
            return null;
        } else {
            return requestModel.getClassifierOptionsResponseModels().stream().filter(
                    responseModel -> isParsableOptions(responseModel.getOptions())).findFirst().orElse(null);
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
     * Creates response with attachment.
     *
     * @param file - file attachment
     * @return response entity
     */
    public static ResponseEntity buildAttachmentResponse(File file) {
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
     * Splits string by "." separator.
     *
     * @param str - string value
     * @return tokens array
     */
    public static String[] splitByPointSeparator(String str) {
        return StringUtils.split(str, POINT_SEPARATOR);
    }

    /**
     * Transform requests statuses map to statistics dto.
     *
     * @param statusStatisticsMap - requests statuses map
     * @return request status statistics dto
     */
    public static RequestStatusStatisticsDto toRequestStatusesStatistics(Map<RequestStatus, Long> statusStatisticsMap) {
        RequestStatusStatisticsDto requestStatusStatisticsDto = new RequestStatusStatisticsDto();
        requestStatusStatisticsDto.setNewRequestsCount(statusStatisticsMap.get(RequestStatus.NEW));
        requestStatusStatisticsDto.setFinishedRequestsCount(statusStatisticsMap.get(RequestStatus.FINISHED));
        requestStatusStatisticsDto.setTimeoutRequestsCount(statusStatisticsMap.get(RequestStatus.TIMEOUT));
        requestStatusStatisticsDto.setErrorRequestsCount(statusStatisticsMap.get(RequestStatus.ERROR));
        requestStatusStatisticsDto.setTotalCount(
                statusStatisticsMap.values().stream().mapToLong(Long::longValue).sum());
        return requestStatusStatisticsDto;
    }
}
