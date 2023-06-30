package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ers.ErsConfig;
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
        Instances data = instancesRequestDataModel.getData();
        log.info(
                "Starting evaluation request with optimal classifier options for data hash [{}], options request id [{}]",
                instancesRequestDataModel.getDataMd5Hash(), instancesRequestDataModel.getRequestId());
        ClassifierOptionsResult classifierOptionsResult = getOptimalClassifierOptions(instancesRequestDataModel);
        if (!classifierOptionsResult.isFound()) {
            EvaluationResultsDataModel evaluationResultsDataModel =
                    buildErrorEvaluationResultsModel(UUID.randomUUID().toString(),
                            classifierOptionsResult.getErrorCode());
            log.info("Response [{}] with error code [{}] has been build for data hash [{}], options request id [{}]",
                    evaluationResultsDataModel.getRequestId(), evaluationResultsDataModel.getErrorCode(),
                    instancesRequestDataModel.getDataMd5Hash(), instancesRequestDataModel.getRequestId());
            return evaluationResultsDataModel;
        } else {
            return evaluateModel(instancesRequestDataModel, classifierOptionsResult.getOptionsJson(), data);
        }
    }

    private ClassifierOptionsResult getOptimalClassifierOptions(InstancesRequestDataModel instancesRequestDataModel) {
        if (isUseClassifierOptionsCache()) {
            return classifierOptionsCacheService.getOptimalClassifierOptionsFromCache(instancesRequestDataModel);
        } else {
            return classifierOptionsCacheService.getOptimalClassifierOptionsFromErs(instancesRequestDataModel);
        }
    }

    private boolean isUseClassifierOptionsCache() {
        return Boolean.TRUE.equals(ersConfig.getUseClassifierOptionsCache());
    }

    private EvaluationResultsDataModel evaluateModel(InstancesRequestDataModel instancesRequestDataModel,
                                                     String options,
                                                     Instances data) {
        log.info("Starting to evaluate model for data hash [{}] with options [{}], options request id [{}]",
                instancesRequestDataModel.getDataMd5Hash(), options, instancesRequestDataModel.getRequestId());
        AbstractClassifier classifier = classifierOptionsAdapter.convert(parseOptions(options));
        EvaluationRequestDataModel evaluationRequest =
                evaluationRequestMapper.map(instancesRequestDataModel, crossValidationConfig);
        evaluationRequest.setData(data);
        evaluationRequest.setClassifier(classifier);
        var evaluationResultsDataModel = evaluationRequestService.processRequest(evaluationRequest);
        log.info("Model has been evaluated for data hash [{}] with options [{}], options request id [{}]",
                instancesRequestDataModel.getDataMd5Hash(), options, instancesRequestDataModel.getRequestId());
        return evaluationResultsDataModel;
    }
}
