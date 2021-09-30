package com.ecaservice.server.service.ers;

import com.ecaservice.common.web.dto.ValidationErrorDto;
import com.ecaservice.server.config.cache.CacheNames;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.ErsErrorCode;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.mapping.ClassifierReportMapper;
import com.ecaservice.server.mapping.ErsResponseStatusMapper;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.repository.ErsRequestRepository;
import eca.core.evaluation.EvaluationResults;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.common.web.util.ValidationErrorHelper.getFirstError;
import static com.ecaservice.common.web.util.ValidationErrorHelper.retrieveValidationErrors;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.server.util.Utils.getFirstClassifierReport;
import static com.ecaservice.server.util.Utils.isValid;

/**
 * Implements service for saving evaluation results by sending request to ERS web - service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErsRequestService {

    private final ErsRequestSender ersRequestSender;
    private final ErsRequestRepository ersRequestRepository;
    private final ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    private final ClassifierReportMapper classifierReportMapper;
    private final ErsResponseStatusMapper ersResponseStatusMapper;

    /**
     * Save evaluation results by sending request to ERS web - service.
     *
     * @param evaluationResults - evaluation results
     * @param ersRequest        - evaluation results service request
     */
    public void saveEvaluationResults(EvaluationResults evaluationResults, ErsRequest ersRequest) {
        ersRequest.setRequestDate(LocalDateTime.now());
        ersRequest.setRequestId(UUID.randomUUID().toString());
        try {
            log.info("Starting to send evaluation results to ERS with request [{}]", ersRequest.getRequestId());
            var resultsResponse = ersRequestSender.sendEvaluationResults(evaluationResults, ersRequest.getRequestId());
            log.info("Received success response for requestId [{}] from ERS.", resultsResponse.getRequestId());
            ersRequest.setResponseStatus(ErsResponseStatus.SUCCESS);
        } catch (FeignException.ServiceUnavailable ex) {
            log.error("Service unavailable error while sending evaluation results: {}", ex.getMessage());
            handleErrorRequest(ersRequest, ErsResponseStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while sending evaluation results: {}", ex.getMessage());
            handleBadRequest(ersRequest, ex);
        } catch (Exception ex) {
            log.error("Unknown an error while sending evaluation results: {}", ex.getMessage());
            handleErrorRequest(ersRequest, ErsResponseStatus.ERROR, ex.getMessage());
        } finally {
            ersRequestRepository.save(ersRequest);
        }
    }

    /**
     * Gets evaluation results simple response.
     *
     * @param requestId - ERS request id
     * @return evaluation results simple response
     */
    @Cacheable(value = CacheNames.EVALUATION_RESULTS_CACHE_NAME)
    public GetEvaluationResultsResponse getEvaluationResults(String requestId) {
        log.info("Starting to get evaluation results simple response for request id [{}]", requestId);
        GetEvaluationResultsRequest request = new GetEvaluationResultsRequest();
        request.setRequestId(requestId);
        GetEvaluationResultsResponse response = ersRequestSender.getEvaluationResultsSimpleResponse(request);
        log.info("Evaluation results simple response with request id [{}] has been fetched", requestId);
        return response;
    }

    /**
     * Finds optimal classifier options report.
     *
     * @param classifierOptionsRequest - classifier options request
     * @param requestModel             - classifier options request entity
     * @return optimal classifier options
     */
    public ClassifierOptionsResult getOptimalClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest,
                                                               ClassifierOptionsRequestModel requestModel) {
        requestModel.setRequestDate(LocalDateTime.now());
        ClassifierOptionsResult classifierOptionsResult = new ClassifierOptionsResult();
        try {
            log.info("Sending request to find classifier optimal options for data '{}'.",
                    classifierOptionsRequest.getRelationName());
            ClassifierOptionsResponse response =
                    ersRequestSender.getClassifierOptions(classifierOptionsRequest);
            log.info("Received response with requestId = {}, for data '{}'", response.getRequestId(),
                    classifierOptionsRequest.getRelationName());
            handleClassifierOptionsResponse(classifierOptionsRequest, response, requestModel, classifierOptionsResult);
        } catch (FeignException.ServiceUnavailable ex) {
            log.error("Service unavailable error while sending classifier options request: {}.", ex.getMessage());
            handleErrorRequest(requestModel, ErsResponseStatus.SERVICE_UNAVAILABLE, ex.getMessage());
            setClassifierOptionsResultError(classifierOptionsResult, requestModel.getResponseStatus().getDescription());
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while sending classifier options request: {}.", ex.getMessage());
            handleBadRequest(requestModel, ex);
            setClassifierOptionsResultError(classifierOptionsResult, requestModel.getResponseStatus().getDescription());
        } catch (Exception ex) {
            log.error("Unknown error while sending classifier options request: {}.", ex.getMessage());
            handleErrorRequest(requestModel, ErsResponseStatus.ERROR, ex.getMessage());
            setClassifierOptionsResultError(classifierOptionsResult, requestModel.getResponseStatus().getDescription());
        } finally {
            classifierOptionsRequestModelRepository.save(requestModel);
        }
        log.info("Got optimal classifier options result [{}] for data [{}]", classifierOptionsResult,
                classifierOptionsRequest.getRelationName());
        return classifierOptionsResult;
    }

    private void handleClassifierOptionsResponse(ClassifierOptionsRequest classifierOptionsRequest,
                                                 ClassifierOptionsResponse response,
                                                 ClassifierOptionsRequestModel requestModel,
                                                 ClassifierOptionsResult classifierOptionsResult) {
        requestModel.setRequestId(response.getRequestId());
        ClassifierReport classifierReport = getFirstClassifierReport(response);
        if (!isValid(classifierReport)) {
            handleErrorRequest(requestModel, ErsResponseStatus.ERROR, "Got empty classifier options string!");
            setClassifierOptionsResultError(classifierOptionsResult, requestModel.getResponseStatus().getDescription());
        } else {
            //Checks classifier options deserialization
            parseOptions(classifierReport.getOptions());
            classifierOptionsResult.setOptionsJson(classifierReport.getOptions());
            classifierOptionsResult.setFound(true);
            log.info("Optimal classifier options [{}] has been found for data '{}'.",
                    classifierReport.getOptions(), classifierOptionsRequest.getRelationName());
            requestModel.setClassifierOptionsResponseModels(
                    Collections.singletonList(classifierReportMapper.map(classifierReport)));
            requestModel.setResponseStatus(ErsResponseStatus.SUCCESS);
        }
    }

    private void setClassifierOptionsResultError(ClassifierOptionsResult classifierOptionsResultError,
                                                 String errorMessage) {
        classifierOptionsResultError.setFound(false);
        classifierOptionsResultError.setErrorMessage(errorMessage);
    }

    private void handleErrorRequest(ErsRequest ersRequest, ErsResponseStatus responseStatus, String errorMessage) {
        ersRequest.setResponseStatus(responseStatus);
        ersRequest.setDetails(errorMessage);
    }

    private void handleBadRequest(ErsRequest ersRequest, FeignException.BadRequest badRequestEx) {
        try {
            var validationErrors = retrieveValidationErrors(badRequestEx.contentUTF8());
            handleValidationError(ersRequest, validationErrors);
            ersRequest.setDetails(badRequestEx.getMessage());
        } catch (Exception ex) {
            log.error("Got error while handling bad request with status [{}] for request id [{}]",
                    badRequestEx.status(), ersRequest.getRequestId());
            handleErrorRequest(ersRequest, ErsResponseStatus.ERROR, ex.getMessage());
        }
    }

    private void handleValidationError(ErsRequest ersRequest, List<ValidationErrorDto> validationErrors) {
        var errorCodes = Stream.of(ErsErrorCode.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        var validationError = getFirstError(errorCodes, validationErrors);
        if (validationError.isEmpty()) {
            log.warn("Got unknown ers error code for request id [{}]. Set ERROR response status",
                    ersRequest.getRequestId());
            ersRequest.setResponseStatus(ErsResponseStatus.ERROR);
        } else {
            ErsErrorCode ersErrorCode = ErsErrorCode.valueOf(validationError.get().getCode());
            ersRequest.setResponseStatus(ersResponseStatusMapper.map(ersErrorCode));
        }
    }
}
