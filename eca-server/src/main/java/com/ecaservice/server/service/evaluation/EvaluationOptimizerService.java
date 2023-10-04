package com.ecaservice.server.service.evaluation;

import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.mapping.EvaluationRequestMapper;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.evaluation.EvaluationMessageRequestDataModel;
import com.ecaservice.server.model.evaluation.EvaluationResultsDataModel;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;

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
    private final EvaluationRequestService evaluationRequestService;
    private final EvaluationRequestMapper evaluationRequestMapper;
    private final ClassifierOptionsAdapter classifierOptionsAdapter;
    private final OptimalClassifierOptionsFetcher optimalClassifierOptionsFetcher;

    /**
     * Evaluate model with optimal classifier options.
     *
     * @param instancesRequestDataModel - instances request
     * @return evaluation response
     */
    public EvaluationResultsDataModel evaluateWithOptimalClassifierOptions(
            InstancesRequestDataModel instancesRequestDataModel) {
        log.info(
                "Starting evaluation request with optimal classifier options for data uuid [{}], options request id [{}]",
                instancesRequestDataModel.getDataUuid(), instancesRequestDataModel.getRequestId());
        ClassifierOptionsResult classifierOptionsResult =
                optimalClassifierOptionsFetcher.getOptimalClassifierOptions(instancesRequestDataModel);
        if (!classifierOptionsResult.isFound()) {
            EvaluationResultsDataModel evaluationResultsDataModel =
                    buildErrorEvaluationResultsModel(UUID.randomUUID().toString(),
                            classifierOptionsResult.getErrorCode());
            log.info("Response [{}] with error code [{}] has been build for data uuid [{}], options request id [{}]",
                    evaluationResultsDataModel.getRequestId(), evaluationResultsDataModel.getErrorCode(),
                    instancesRequestDataModel.getDataUuid(), instancesRequestDataModel.getRequestId());
            return evaluationResultsDataModel;
        } else {
            return evaluateModel(instancesRequestDataModel, classifierOptionsResult.getOptionsJson());
        }
    }

    private EvaluationResultsDataModel evaluateModel(InstancesRequestDataModel instancesRequestDataModel,
                                                     String options) {
        log.info("Starting to evaluate model for data uuid [{}] with options [{}], options request id [{}]",
                instancesRequestDataModel.getDataUuid(), options, instancesRequestDataModel.getRequestId());
        AbstractClassifier classifier = classifierOptionsAdapter.convert(parseOptions(options));
        EvaluationMessageRequestDataModel evaluationRequest =
                evaluationRequestMapper.map(instancesRequestDataModel, crossValidationConfig);
        evaluationRequest.setClassifier(classifier);
        evaluationRequest.setRequestId(UUID.randomUUID().toString());
        var evaluationResultsDataModel =
                evaluationRequestService.createAndProcessRequest(evaluationRequest);
        log.info("Model has been evaluated for data uuid [{}] with options [{}], options request id [{}]",
                instancesRequestDataModel.getDataUuid(), options, instancesRequestDataModel.getRequestId());
        return evaluationResultsDataModel;
    }
}
