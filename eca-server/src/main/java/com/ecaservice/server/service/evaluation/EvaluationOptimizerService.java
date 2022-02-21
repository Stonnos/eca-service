package com.ecaservice.server.service.evaluation;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.mapping.ClassifierOptionsRequestMapper;
import com.ecaservice.server.mapping.EvaluationRequestMapper;
import com.ecaservice.server.model.ClassifierOptionsResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.UUID;

import static com.ecaservice.server.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.server.util.Utils.buildEvaluationErrorResponse;

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
     * @param instancesRequest - instances request
     * @return evaluation response
     */
    public EvaluationResponse evaluateWithOptimalClassifierOptions(InstancesRequest instancesRequest) {
        Instances data = instancesRequest.getData();
        log.info("Starting evaluation with optimal classifier options for data '{}'",
                data.relationName());
        ClassifierOptionsRequest classifierOptionsRequest =
                classifierOptionsRequestMapper.map(instancesRequest, crossValidationConfig);
        classifierOptionsRequest.setRequestId(UUID.randomUUID().toString());
        ClassifierOptionsResult classifierOptionsResult = getOptimalClassifierOptions(classifierOptionsRequest);
        if (!classifierOptionsResult.isFound()) {
            return buildEvaluationErrorResponse(classifierOptionsResult.getErrorCode());
        } else {
            return evaluateModel(classifierOptionsRequest, classifierOptionsResult.getOptionsJson(), data);
        }
    }

    private ClassifierOptionsResult getOptimalClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        String dataMd5Hash = classifierOptionsRequest.getDataHash();
        if (isUseClassifierOptionsCache()) {
            return classifierOptionsCacheService.getOptimalClassifierOptionsFromCache(classifierOptionsRequest,
                    dataMd5Hash);
        } else {
            return classifierOptionsCacheService.getOptimalClassifierOptionsFromErs(classifierOptionsRequest,
                    dataMd5Hash);
        }
    }

    private boolean isUseClassifierOptionsCache() {
        return Boolean.TRUE.equals(ersConfig.getUseClassifierOptionsCache());
    }

    private EvaluationResponse evaluateModel(ClassifierOptionsRequest classifierOptionsRequest, String options,
                                             Instances data) {
        log.info("Starting to evaluate model for data [{}] with options [{}]", data.relationName(), options);
        AbstractClassifier classifier = classifierOptionsAdapter.convert(parseOptions(options));
        EvaluationRequest evaluationRequest = evaluationRequestMapper.map(classifierOptionsRequest);
        evaluationRequest.setData(data);
        evaluationRequest.setClassifier(classifier);
        return evaluationRequestService.processRequest(evaluationRequest);
    }
}
