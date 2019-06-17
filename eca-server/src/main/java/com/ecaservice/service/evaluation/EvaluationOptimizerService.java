package com.ecaservice.service.evaluation;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.CrossValidationConfig;
import com.ecaservice.dto.EvaluationRequest;
import com.ecaservice.dto.EvaluationResponse;
import com.ecaservice.dto.InstancesRequest;
import com.ecaservice.dto.evaluation.ClassifierOptionsRequest;
import com.ecaservice.dto.evaluation.ResponseStatus;
import com.ecaservice.mapping.ClassifierOptionsRequestMapper;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.mapping.EvaluationRequestMapper;
import com.ecaservice.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.service.ClassifierOptionsService;
import com.ecaservice.service.ers.ErsRequestService;
import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.ecaservice.util.Utils.buildErrorResponse;
import static com.ecaservice.util.Utils.getFirstResponseModel;
import static com.ecaservice.util.Utils.parseOptions;

/**
 * Implements classifier evaluation by searching optimal classifier options.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class EvaluationOptimizerService {

    private static final String RESULTS_NOT_FOUND_MESSAGE = "Can't find classifiers options for data '%s'";

    private final CrossValidationConfig crossValidationConfig;
    private final CommonConfig commonConfig;
    private final EvaluationRequestService evaluationRequestService;
    private final ErsRequestService ersRequestService;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final EvaluationRequestMapper evaluationRequestMapper;
    private final ClassifierOptionsRequestMapper classifierOptionsRequestMapper;
    private final ClassifierOptionsService classifierOptionsService;
    private final ClassifierOptionsRequestRepository classifierOptionsRequestRepository;

    private ConcurrentHashMap<String, Object> dataMd5Hashes = new ConcurrentHashMap<>();

    /**
     * Constructor with spring dependency injection.
     *
     * @param crossValidationConfig               - cross - validation config bean
     * @param commonConfig                        - common config bean
     * @param evaluationRequestService            - evaluation request service bean
     * @param classifierOptionsRequestModelMapper - classifier options request model mapper bean
     * @param ersRequestService                   - ers request service bean
     * @param evaluationRequestMapper             - evaluation request mapper bean
     * @param classifierOptionsRequestMapper      - classifier options request mapper bean
     * @param classifierOptionsService            - classifier options service bean
     * @param classifierOptionsRequestRepository  - classifier options request repository bean
     */
    @Inject
    public EvaluationOptimizerService(CrossValidationConfig crossValidationConfig,
                                      CommonConfig commonConfig,
                                      EvaluationRequestService evaluationRequestService,
                                      ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper,
                                      ErsRequestService ersRequestService,
                                      EvaluationRequestMapper evaluationRequestMapper,
                                      ClassifierOptionsRequestMapper classifierOptionsRequestMapper,
                                      ClassifierOptionsService classifierOptionsService,
                                      ClassifierOptionsRequestRepository classifierOptionsRequestRepository) {
        this.crossValidationConfig = crossValidationConfig;
        this.commonConfig = commonConfig;
        this.evaluationRequestService = evaluationRequestService;
        this.ersRequestService = ersRequestService;
        this.classifierOptionsRequestModelMapper = classifierOptionsRequestModelMapper;
        this.evaluationRequestMapper = evaluationRequestMapper;
        this.classifierOptionsRequestMapper = classifierOptionsRequestMapper;
        this.classifierOptionsService = classifierOptionsService;
        this.classifierOptionsRequestRepository = classifierOptionsRequestRepository;
    }

    /**
     * Evaluate model with optimal classifier options.
     *
     * @param instancesRequest - instances request
     * @return evaluation response
     */
    public EvaluationResponse evaluateWithOptimalClassifierOptions(InstancesRequest instancesRequest) {
        if (!Optional.ofNullable(instancesRequest).map(InstancesRequest::getData).isPresent()) {
            throw new IllegalArgumentException("Instances isn't specified!");
        }
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
                classifierOptionsRequest.getInstances().getXmlInstances().getBytes(Charsets.UTF_8));
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
                        Collections.singleton(ResponseStatus.SUCCESS),
                        LocalDateTime.now().minusDays(commonConfig.getClassifierOptionsCacheDurationInDays()),
                        PageRequest.of(0, 1));
        return requestModels.stream().findFirst().map(
                ClassifierOptionsRequestEntity::getClassifierOptionsRequestModel).orElse(null);
    }

    private EvaluationResponse evaluateModel(ClassifierOptionsRequest classifierOptionsRequest, String options,
                                             Instances data) {
        try {
            AbstractClassifier classifier = classifierOptionsService.convert(parseOptions(options));
            EvaluationRequest evaluationRequest = evaluationRequestMapper.map(classifierOptionsRequest);
            evaluationRequest.setData(data);
            evaluationRequest.setClassifier(classifier);
            return evaluationRequestService.processRequest(evaluationRequest);
        } catch (Exception ex) {
            log.error("There was an error: {}", ex.getMessage());
        }
        return buildErrorResponse(String.format(RESULTS_NOT_FOUND_MESSAGE, data.relationName()));
    }
}
