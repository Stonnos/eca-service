package com.ecaservice.service.evaluation;

import com.ecaservice.base.model.EvaluationRequest;
import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.base.model.InstancesRequest;
import com.ecaservice.classifier.options.adapter.ClassifierOptionsAdapter;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.config.ers.ErsConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.mapping.ClassifierOptionsRequestMapper;
import com.ecaservice.mapping.EvaluationRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import static com.ecaservice.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.util.Utils.buildEvaluationErrorResponse;

/**
 * Implements classifier evaluation by searching optimal classifier options.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationOptimizerService {

    private static final String RESULTS_NOT_FOUND_MESSAGE = "Can't find classifiers options for data '%s'";

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
        String optimalOptions = getOptimalClassifierOptions(classifierOptionsRequest);
        if (optimalOptions == null) {
            return buildEvaluationErrorResponse(String.format(RESULTS_NOT_FOUND_MESSAGE, data.relationName()));
        } else {
            return evaluateModel(classifierOptionsRequest, optimalOptions, data);
        }
    }

    private String getOptimalClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
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
