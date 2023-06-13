package com.ecaservice.server.service.ers;

import com.ecaservice.base.model.ErrorCode;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.server.config.cache.CacheNames;
import com.ecaservice.server.mapping.ClassifierReportMapper;
import com.ecaservice.server.mapping.ErsResponseStatusMapper;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.ErsEvaluationRequestData;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ErsRequest;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.server.repository.ErsRequestRepository;
import com.ecaservice.server.service.evaluation.EvaluationResultsService;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.server.util.Utils.getFirstClassifierReport;
import static com.ecaservice.server.util.Utils.isValid;

/**
 * Implements service for saving evaluation results by sending request to ERS service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ErsRequestService {

    private final ErsClient ersClient;
    private final ErsRequestSender ersRequestSender;
    private final EvaluationResultsService evaluationResultsService;
    private final ErsRequestRepository ersRequestRepository;
    private final ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    private final ClassifierReportMapper classifierReportMapper;
    private final ErsErrorHandler ersErrorHandler;
    private final ErsResponseStatusMapper ersResponseStatusMapper;

    /**
     * Save evaluation results by sending request to ERS service.
     *
     * @param ersEvaluationRequestData - ers evaluation request data
     */
    public void saveEvaluationResults(ErsEvaluationRequestData ersEvaluationRequestData) {
        var ersRequest = ersEvaluationRequestData.getErsRequest();
        ersRequest.setRequestId(UUID.randomUUID().toString());
        try {
            EvaluationResultsRequest evaluationResultsRequest = evaluationResultsService.proceed(ersEvaluationRequestData);
            evaluationResultsRequest.setRequestId(ersRequest.getRequestId());
            saveEvaluationResults(evaluationResultsRequest, ersRequest);
        } catch (Exception ex) {
            log.error("Unknown an error while sending evaluation results: {}", ex.getMessage());
            ersErrorHandler.handleErrorRequest(ersRequest, ErsResponseStatus.ERROR, ex.getMessage());
        }
    }

    /**
     * Save evaluation results by sending request to ERS service.
     *
     * @param evaluationResultsRequest - evaluation results request
     * @param ersRequest               - evaluation results service request
     */
    public void saveEvaluationResults(EvaluationResultsRequest evaluationResultsRequest, ErsRequest ersRequest) {
        try {
            ersRequest.setRequestDate(LocalDateTime.now());
            ersRequestSender.send(evaluationResultsRequest);
            ersRequest.setResponseStatus(ErsResponseStatus.SUCCESS);
            ersRequestRepository.save(ersRequest);
        } catch (FeignException.ServiceUnavailable | RetryableException ex) {
            log.error("Service unavailable error [{}] while sending evaluation results: {}",
                    ex.getClass().getSimpleName(), ex.getMessage());
            ersErrorHandler.handleErrorRequest(ersRequest, ErsResponseStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while sending evaluation results: {}", ex.getMessage());
            ersErrorHandler.handleBadRequest(ersRequest, ex);
        } catch (Exception ex) {
            log.error("Unknown error while sending evaluation results: {}", ex.getMessage());
            ersErrorHandler.handleErrorRequest(ersRequest, ErsResponseStatus.ERROR, ex.getMessage());
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
        GetEvaluationResultsResponse response = ersClient.getEvaluationResults(request);
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
            log.info("Sending request [{}] to find classifier optimal options for data '{}'.",
                    classifierOptionsRequest.getRequestId(), classifierOptionsRequest.getRelationName());
            ClassifierOptionsResponse response = ersClient.getClassifierOptions(classifierOptionsRequest);
            log.info("Received response for request id [{}], data [{}]", response.getRequestId(),
                    classifierOptionsRequest.getRelationName());
            handleClassifierOptionsResponse(classifierOptionsRequest, response, requestModel, classifierOptionsResult);
        } catch (FeignException.ServiceUnavailable | RetryableException ex) {
            log.error("Service unavailable error while sending classifier options request: {}.", ex.getMessage());
            ersErrorHandler.handleErrorRequest(requestModel, ErsResponseStatus.SERVICE_UNAVAILABLE, ex.getMessage());
            setClassifierOptionsResultError(classifierOptionsResult, ErrorCode.SERVICE_UNAVAILABLE);
        } catch (FeignException.BadRequest ex) {
            log.error("Bad request error while sending classifier options request: {}.", ex.getMessage());
            handleBadRequest(requestModel, classifierOptionsResult, ex);
        } catch (Exception ex) {
            log.error("Unknown error while sending classifier options request: {}.", ex.getMessage());
            ersErrorHandler.handleErrorRequest(requestModel, ErsResponseStatus.ERROR, ex.getMessage());
            setClassifierOptionsResultError(classifierOptionsResult, ErrorCode.INTERNAL_SERVER_ERROR);
        }
        log.info("Got optimal classifier options result [{}] for data [{}]", classifierOptionsResult,
                classifierOptionsRequest.getRelationName());
        return classifierOptionsResult;
    }

    private void handleClassifierOptionsResponse(ClassifierOptionsRequest classifierOptionsRequest,
                                                 ClassifierOptionsResponse response,
                                                 ClassifierOptionsRequestModel requestModel,
                                                 ClassifierOptionsResult classifierOptionsResult) {
        ClassifierReport classifierReport = getFirstClassifierReport(response);
        if (!isValid(classifierReport)) {
            ersErrorHandler.handleErrorRequest(requestModel, ErsResponseStatus.ERROR,
                    "Got empty classifier options string!");
            setClassifierOptionsResultError(classifierOptionsResult, ErrorCode.INTERNAL_SERVER_ERROR);
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
            classifierOptionsRequestModelRepository.save(requestModel);
        }
    }

    private void setClassifierOptionsResultError(ClassifierOptionsResult classifierOptionsResultError,
                                                 ErrorCode errorCode) {
        classifierOptionsResultError.setFound(false);
        classifierOptionsResultError.setErrorCode(errorCode);
    }

    private void handleBadRequest(ErsRequest ersRequest,
                                  ClassifierOptionsResult classifierOptionsResult,
                                  FeignException.BadRequest badRequestEx) {
        var ersErrorCode = ersErrorHandler.handleBadRequest(ersRequest, badRequestEx);
        if (ersErrorCode == null) {
            setClassifierOptionsResultError(classifierOptionsResult, ErrorCode.INTERNAL_SERVER_ERROR);
        } else {
            var errorCode = ersResponseStatusMapper.mapErrorCode(ersErrorCode);
            setClassifierOptionsResultError(classifierOptionsResult, errorCode);
        }
    }
}
