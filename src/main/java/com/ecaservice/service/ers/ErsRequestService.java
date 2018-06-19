package com.ecaservice.service.ers;

import com.ecaservice.config.EcaServiceParam;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsResponse;
import com.ecaservice.dto.evaluation.ClassifierReport;
import com.ecaservice.dto.evaluation.EvaluationResultsResponse;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassifierReportMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ErsRequest;
import com.ecaservice.repository.ClassifierOptionsRequestModelRepository;
import com.ecaservice.repository.ErsRequestRepository;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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

    /**
     * Constructor with spring dependency injection.
     *
     * @param ersWebServiceClient                     - ers web service client bean
     * @param ersRequestRepository                    - evaluation results service request repository bean
     * @param classifierOptionsRequestModelRepository - classifier options request model repository bean
     * @param classifierReportMapper                  - classifier report mapper bean
     */
    @Inject
    public ErsRequestService(ErsWebServiceClient ersWebServiceClient,
                             ErsRequestRepository ersRequestRepository,
                             ClassifierOptionsRequestModelRepository classifierOptionsRequestModelRepository,
                             ClassifierReportMapper classifierReportMapper) {
        this.ersWebServiceClient = ersWebServiceClient;
        this.ersRequestRepository = ersRequestRepository;
        this.classifierOptionsRequestModelRepository = classifierOptionsRequestModelRepository;
        this.classifierReportMapper = classifierReportMapper;
    }

    /**
     * Save evaluation results by sending request to ERS web - service.
     *
     * @param evaluationResults - evaluation results
     * @param ersRequest        - evaluation results service request
     */
    @Async(EcaServiceParam.EVALUATION_RESULTS_POOL_EXECUTOR)
    public void saveEvaluationResults(EvaluationResults evaluationResults, ErsRequest ersRequest) {
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

    /**
     * Finds optimal classifier options report.
     *
     * @param classifierOptionsRequest - classifier options request
     * @param requestModel             - classifier options request entity
     * @return classifier report
     */
    public ClassifierReport getOptimalClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest,
                                                        ClassifierOptionsRequestModel requestModel) {
        requestModel.setRequestDate(LocalDateTime.now());
        ClassifierReport classifierReport = null;
        try {
            log.trace("Sending request to find classifier optimal options for data '{}'.",
                    classifierOptionsRequest.getInstances().getRelationName());
            ClassifierOptionsResponse response =
                    ersWebServiceClient.getClassifierOptions(classifierOptionsRequest);
            log.trace("Received response with requestId = {}, status = {}", response.getRequestId(),
                    response.getStatus());
            requestModel.setRequestId(response.getRequestId());
            requestModel.setResponseStatus(response.getStatus());
            if (ResponseStatus.SUCCESS.equals(response.getStatus())) {
                classifierReport = response.getClassifierReports().stream().findFirst().orElse(null);
                if (!isValid(classifierReport)) {
                    handleErrorRequest(requestModel, "Got empty classifier options string!");
                } else {
                    //Checks classifier options deserialization
                    parseOptions(classifierReport.getOptions());
                    log.info("Optimal classifier options [{}] has been found for data '{}'.",
                            classifierReport.getOptions(), classifierOptionsRequest.getInstances().getRelationName());
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
        return classifierReport;
    }

    private void handleErrorRequest(ClassifierOptionsRequestModel requestModel, String errorMessage) {
        requestModel.setResponseStatus(ResponseStatus.ERROR);
        requestModel.setDetails(errorMessage);
    }
}
