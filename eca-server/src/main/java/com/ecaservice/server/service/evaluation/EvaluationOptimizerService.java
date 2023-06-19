package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.mapping.ClassifierOptionsRequestMapper;
import com.ecaservice.server.mapping.EvaluationRequestMapper;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.evaluation.EvaluationRequestDataModel;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.UUID;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.server.util.Utils.buildErrorEvaluationResultsModel;

/**
 * Implements classifier evaluation by searching optimal classifier options.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationOptimizerService {

    private final CrossValidationConfig crossValidationConfig;
    private final ErsConfig ersConfig;
    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationRequestMapper evaluationRequestMapper;
    private final ClassifierOptionsRequestMapper classifierOptionsRequestMapper;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final ClassifierOptionsCacheService classifierOptionsCacheService;

    /**
     * Evaluate model with optimal classifier options.
     *
     * @param instancesRequestDataModel - instances request
     * @return evaluation response
     */
    public EvaluationResultsDataModel evaluateWithOptimalClassifierOptions(
            InstancesRequestDataModel instancesRequestDataModel) {
        String requestId = UUID.randomUUID().toString();
        putMdc(TX_ID, requestId);
        Instances data = instancesRequestDataModel.getData();
        ClassifierOptionsRequest classifierOptionsRequest =
                classifierOptionsRequestMapper.map(instancesRequestDataModel, crossValidationConfig);
        log.info("Starting evaluation request with optimal classifier options for data hash '{}', request id [{}]",
                classifierOptionsRequest.getDataHash(), requestId);
        classifierOptionsRequest.setRequestId(requestId);
        ClassifierOptionsResult classifierOptionsResult = getOptimalClassifierOptions(classifierOptionsRequest);
        if (!classifierOptionsResult.isFound()) {
            return buildErrorEvaluationResultsModel(requestId, classifierOptionsResult.getErrorCode());
        } else {
            return evaluateModel(classifierOptionsRequest, classifierOptionsResult.getOptionsJson(), data);
        }
    }

    private ClassifierOptionsResult getOptimalClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        if (isUseClassifierOptionsCache()) {
            return classifierOptionsCacheService.getOptimalClassifierOptionsFromCache(classifierOptionsRequest);
        } else {
            return classifierOptionsCacheService.getOptimalClassifierOptionsFromErs(classifierOptionsRequest);
        }
    }

    private boolean isUseClassifierOptionsCache() {
        return Boolean.TRUE.equals(ersConfig.getUseClassifierOptionsCache());
    }

    private EvaluationResultsDataModel evaluateModel(ClassifierOptionsRequest classifierOptionsRequest,
                                                     String options,
                                                     Instances data) {
        log.info("Starting to evaluate model for data hash [{}] with options [{}], options request id [{}]",
                classifierOptionsRequest.getDataHash(), options, classifierOptionsRequest.getRequestId());
        AbstractClassifier classifier = classifierOptionsAdapter.convert(parseOptions(options));
        EvaluationRequestDataModel evaluationRequest = evaluationRequestMapper.map(classifierOptionsRequest);
        evaluationRequest.setData(data);
        evaluationRequest.setClassifier(classifier);
        var evaluationResultsDataModel = evaluationRequestService.processRequest(evaluationRequest);
        log.info("Model has been evaluated for data hash [{}] with options [{}], options request id [{}]",
                classifierOptionsRequest.getDataHash(), options, classifierOptionsRequest.getRequestId());
        return evaluationResultsDataModel;
    }
}
