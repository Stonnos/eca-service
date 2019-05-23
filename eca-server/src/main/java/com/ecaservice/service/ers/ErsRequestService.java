package com.ecaservice.service.ers;

import com.ecaservice.config.EcaServiceParam;
import com.ecaservice.config.ErsConfig;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.GetEvaluationResultsRequest;
import com.ecaservice.dto.evaluation.GetEvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassifierReportMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.repository.ErsRequestRepository;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static com.ecaservice.util.Utils.isValid;
import static com.ecaservice.util.Utils.parseOptions;

/**
 * Implements service for saving evaluation results by sending request to ERS web - service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class ErsRequestService {

    private final ErsWebServiceClient ersWebServiceClient;
    private final ErsRequestRepository ersRequestRepository;
    private final ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository;
    private final ClassifierReportMapper classifierReportMapper;
    private final ErsConfig ersConfig;

    /**
     * Constructor with spring dependency injection.
     *
     * @param ersWebServiceClient                     - ers web service client bean
     * @param ersRequestRepository                    - evaluation results service request repository bean
     * @param classifierOptionsRequestModelRepository - classifier options request model repository bean
     * @param classifierReportMapper                  - classifier report mapper bean
     * @param ersConfig                               - evaluation results config bean
     */
    @Inject
    public ErsRequestService(ErsWebServiceClient ersWebServiceClient,
                             ErsRequestRepository ersRequestRepository,
                             ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository,
                             ClassifierReportMapper classifierReportMapper, ErsConfig ersConfig) {
        this.ersWebServiceClient = ersWebServiceClient;
        this.ersRequestRepository = ersRequestRepository;
        this.classifierOptionsRequestModelRepository = classifierOptionsRequestModelRepository;
        this.classifierReportMapper = classifierReportMapper;
        this.ersConfig = ersConfig;
    }

    /**
     * Save evaluation results by sending request to ERS web - service.
     *
     * @param evaluationResults - evaluation results
     * @param ersRequest        - evaluation results service request
     */
    public void saveEvaluationResults(EvaluationResults evaluationResults, ErsRequest ersRequest) {
        if (!Boolean.TRUE.equals(ersConfig.getEnabled())) {
            log.warn("Evaluation results sending is disabled.");
        } else {
            ersRequest.setRequestDate(LocalDateTime.now());
            ersRequest.setRequestId(UUID.randomUUID().toString());
            try {
                EvaluationResultsResponse resultsResponse =
                        ersWebServiceClient.sendEvaluationResults(evaluationResults, ersRequest.getRequestId());
                ersRequest.setResponseStatus(resultsResponse.getStatus());
            } catch (Exception ex) {
                log.error("There was an error while sending evaluation results: {}", ex.getMessage());
                ersRequest.setResponseStatus(ResponseStatus.ERROR);
                ersRequest.setDetails(ex.getMessage());
            } finally {
                ersRequestRepository.save(ersRequest);
            }
        }
    }

    /**
     * Gets evaluation results simple response.
     *
     * @param requestId - ERS request id
     * @return evaluation results simple response
     */
    @Cacheable(value = EcaServiceParam.EVALUATION_RESULTS_CACHE_NAME, unless = "#result.status.name() != 'SUCCESS'")
    public GetEvaluationResultsResponse getEvaluationResults(String requestId) {
        log.info("Starting to get evaluation results simple response for request id [{}]", requestId);
        GetEvaluationResultsRequest request = new GetEvaluationResultsRequest();
        request.setRequestId(requestId);
        GetEvaluationResultsResponse response = ersWebServiceClient.getEvaluationResultsSimpleResponse(request);
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
                    ersWebServiceClient.getClassifierOptions(classifierOptionsRequest);
            log.info("Received response with requestId = {}, status = {} for data '{}'", response.getRequestId(),
                    response.getStatus(), classifierOptionsRequest.getInstances().getRelationName());
            requestModel.setRequestId(response.getRequestId());
            requestModel.setResponseStatus(response.getStatus());
            if (ResponseStatus.SUCCESS.equals(response.getStatus())) {
                ClassifierReport classifierReport = response.getClassifierReports().stream().findFirst().orElse(null);
                if (!isValid(classifierReport)) {
                    handleErrorRequest(requestModel, "Got empty classifier options string!");
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
        } catch (Exception ex) {
            log.error("There was an error while sending classifier options request: {}.", ex.getMessage());
            handleErrorRequest(requestModel, ex.getMessage());
        } finally {
            classifierOptionsRequestModelRepository.save(requestModel);
        }
        return classifierOptions;
    }

    private void handleErrorRequest(ClassifierOptionsRequestModel requestModel, String errorMessage) {
        requestModel.setResponseStatus(ResponseStatus.ERROR);
        requestModel.setDetails(errorMessage);
    }
}
