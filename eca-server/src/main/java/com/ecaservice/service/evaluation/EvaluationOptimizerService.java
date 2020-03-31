package com.ecaservice.service.evaluation;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.conversion.ClassifierOptionsConverter;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.mapping.ClassifierOptionsRequestMapper;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.mapping.EvaluationRequestMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.ecaservice.util.ClassifierOptionsHelper.parseOptions;
import static com.ecaservice.util.Utils.buildErrorResponse;
import static com.ecaservice.util.Utils.getFirstResponseModel;

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
    private final CommonConfig commonConfig;
    private final EvaluationRequestService evaluationRequestService;
    private final ErsRequestService ersRequestService;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final EvaluationRequestMapper evaluationRequestMapper;
    private final ClassifierOptionsRequestMapper classifierOptionsRequestMapper;
    private final ClassifierOptionsConverter classifierOptionsConverter;
    private final ClassifierOptionsRequestRepository classifierOptionsRequestRepository;

    private ConcurrentHashMap<String, Object> dataMd5Hashes = new ConcurrentHashMap<>();

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
        return optimalOptions != null ? evaluateModel(classifierOptionsRequest, optimalOptions, data) :
                buildErrorResponse(String.format(RESULTS_NOT_FOUND_MESSAGE, data.relationName()));
    }

    private String getOptimalClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest) {
        String options;
        String dataMd5Hash = DigestUtils.md5DigestAsHex(
                classifierOptionsRequest.getInstances().getXmlInstances().getBytes(StandardCharsets.UTF_8));
        dataMd5Hashes.putIfAbsent(dataMd5Hash, new Object());
        synchronized (dataMd5Hashes.get(dataMd5Hash)) {
            ClassifierOptionsRequestEntity requestEntity = new ClassifierOptionsRequestEntity();
            requestEntity.setCreationDate(LocalDateTime.now());
            ClassifierOptionsRequestModel requestModel = getLastClassifierOptionsRequestModel(dataMd5Hash);
            ClassifierOptionsResponseModel responseModel = getFirstResponseModel(requestModel);
            if (responseModel != null) {
                log.info("Optimal classifier options [{}] has been taken from last response for data '{}'.",
                        responseModel.getOptions(), classifierOptionsRequest.getInstances().getRelationName());
                requestEntity.setSource(ClassifierOptionsRequestSource.CACHE);
                options = responseModel.getOptions();
            } else {
                requestModel = classifierOptionsRequestModelMapper.map(classifierOptionsRequest);
                requestModel.setRelationName(classifierOptionsRequest.getInstances().getRelationName());
                requestModel.setDataMd5Hash(dataMd5Hash);
                requestEntity.setSource(ClassifierOptionsRequestSource.ERS);
                options = ersRequestService.getOptimalClassifierOptions(classifierOptionsRequest, requestModel);
            }
            requestEntity.setClassifierOptionsRequestModel(requestModel);
            classifierOptionsRequestRepository.save(requestEntity);
        }
        dataMd5Hashes.remove(dataMd5Hash);
        return options;
    }

    private ClassifierOptionsRequestModel getLastClassifierOptionsRequestModel(String dataMd5Hash) {
        List<ClassifierOptionsRequestEntity> requestModels =
                classifierOptionsRequestRepository.findLastRequests(dataMd5Hash,
                        Collections.singletonList(ErsResponseStatus.SUCCESS),
                        LocalDateTime.now().minusDays(commonConfig.getClassifierOptionsCacheDurationInDays()),
                        PageRequest.of(0, 1));
        return requestModels.stream().findFirst().map(
                ClassifierOptionsRequestEntity::getClassifierOptionsRequestModel).orElse(null);
    }

    private EvaluationResponse evaluateModel(ClassifierOptionsRequest classifierOptionsRequest, String options,
                                             Instances data) {
        AbstractClassifier classifier = classifierOptionsConverter.convert(parseOptions(options));
        EvaluationRequest evaluationRequest = evaluationRequestMapper.map(classifierOptionsRequest);
        evaluationRequest.setData(data);
        evaluationRequest.setClassifier(classifier);
        return evaluationRequestService.processRequest(evaluationRequest);
    }
}
