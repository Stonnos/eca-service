package com.ecaservice.service.ers;

import com.ecaservice.config.cache.CacheNames;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.ClassifierReport;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.ers.dto.ResponseStatus;
import com.ecaservice.mapping.ClassifierReportMapper;
import com.ecaservice.mapping.ErsResponseStatusMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.repository.ErsRequestRepository;
import eca.core.evaluation.EvaluationResults;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.util.Utils.getFirstClassifierReport;
import static com.ecaservice.util.Utils.isValid;

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
            EvaluationResultsResponse resultsResponse =
                    ersRequestSender.sendEvaluationResults(evaluationResults, ersRequest.getRequestId());
            log.info("Received response for requestId [{}] with status = {} from ERS.",
                    resultsResponse.getRequestId(), resultsResponse.getStatus());
            ersRequest.setResponseStatus(ersResponseStatusMapper.map(resultsResponse.getStatus()));
        } catch (FeignException.ServiceUnavailable ex) {
            log.error("There was an error while sending evaluation results: {}", ex.getMessage());
            handleErrorRequest(ersRequest, ErsResponseStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        } catch (Exception ex) {
            log.error("There was an error while sending evaluation results: {}", ex.getMessage());
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
    @Cacheable(value = CacheNames.EVALUATION_RESULTS_CACHE_NAME, unless = "#result.status.name() != 'SUCCESS'")
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
     * @return optimal classifier options json string
     */
    public String getOptimalClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest,
                                              ClassifierOptionsRequestModel requestModel) {
        requestModel.setRequestDate(LocalDateTime.now());
        String classifierOptions = null;
        try {
            log.info("Sending request to find classifier optimal options for data '{}'.",
                    classifierOptionsRequest.getInstances().getRelationName());
            ClassifierOptionsResponse response =
                    ersRequestSender.getClassifierOptions(classifierOptionsRequest);
            log.info("Received response with requestId = {}, status = {} for data '{}'", response.getRequestId(),
                    response.getStatus(), classifierOptionsRequest.getInstances().getRelationName());
            requestModel.setRequestId(response.getRequestId());
            requestModel.setResponseStatus(ersResponseStatusMapper.map(response.getStatus()));
            if (ResponseStatus.SUCCESS.equals(response.getStatus())) {
                ClassifierReport classifierReport = getFirstClassifierReport(response);
                if (!isValid(classifierReport)) {
                    handleErrorRequest(requestModel, ErsResponseStatus.ERROR, "Got empty classifier options string!");
                } else {
                    //Checks classifier options deserialization
                    parseOptions(classifierReport.getOptions());
                    classifierOptions = classifierReport.getOptions();
                    log.info("Optimal classifier options [{}] has been found for data '{}'.", classifierOptions,
                            classifierOptionsRequest.getInstances().getRelationName());
                    requestModel.setClassifierOptionsResponseModels(
                            Collections.singletonList(classifierReportMapper.map(classifierReport)));
                }
            }
        } catch (FeignException.ServiceUnavailable ex) {
            log.error("There was an error while sending classifier options request: {}.", ex.getMessage());
            handleErrorRequest(requestModel, ErsResponseStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        } catch (Exception ex) {
            log.error("There was an error while sending classifier options request: {}.", ex.getMessage());
            handleErrorRequest(requestModel, ErsResponseStatus.ERROR, ex.getMessage());
        } finally {
            classifierOptionsRequestModelRepository.save(requestModel);
        }
        return classifierOptions;
    }

    private void handleErrorRequest(ErsRequest ersRequest, ErsResponseStatus responseStatus, String errorMessage) {
        ersRequest.setResponseStatus(responseStatus);
        ersRequest.setDetails(errorMessage);
    }
}
