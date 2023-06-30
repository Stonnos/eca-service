package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.CrossValidationConfig;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.mapping.ClassifierOptionsRequestMapper;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import com.ecaservice.server.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.server.service.InstancesInfoService;
import com.ecaservice.server.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.server.util.Utils.createClassifierOptionsRequestEntity;
import static com.ecaservice.server.util.Utils.getFirstResponseModel;

/**
 * Classifier options cache service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierOptionsCacheService {

    private final ErsConfig ersConfig;
    private final CrossValidationConfig crossValidationConfig;
    private final ErsRequestService ersRequestService;
    private final InstancesInfoService instancesInfoService;
    private final ClassifierOptionsRequestMapper classifierOptionsRequestMapper;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ClassifierOptionsRequestRepository classifierOptionsRequestRepository;

    /**
     * Gets optimal classifiers options from ERS service.
     *
     * @param instancesRequestDataModel - instances request data model
     * @return classifier options result
     */
    public ClassifierOptionsResult getOptimalClassifierOptionsFromErs(
            InstancesRequestDataModel instancesRequestDataModel) {
        log.info("Starting to get optimal classifiers options from ERS for data md5 hash: {}, options req id [{}]",
                instancesRequestDataModel.getDataMd5Hash(), instancesRequestDataModel.getRequestId());
        var classifierOptionsRequest =
                classifierOptionsRequestMapper.map(instancesRequestDataModel, crossValidationConfig);
        ClassifierOptionsRequestEntity requestEntity =
                createClassifierOptionsRequestEntity(ClassifierOptionsRequestSource.ERS);
        requestEntity.setRequestId(classifierOptionsRequest.getRequestId());
        ClassifierOptionsRequestModel requestModel = classifierOptionsRequestModelMapper.map(classifierOptionsRequest);
        InstancesInfo instancesInfo =
                instancesInfoService.getOrSaveInstancesInfo(instancesRequestDataModel.getDataMd5Hash(),
                        instancesRequestDataModel.getData());
        requestModel.setInstancesInfo(instancesInfo);
        ClassifierOptionsResult classifierOptionsResult =
                ersRequestService.getOptimalClassifierOptions(classifierOptionsRequest, requestModel);
        requestEntity.setClassifierOptionsRequestModel(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        return classifierOptionsResult;
    }

    /**
     * Gets optimal classifiers options from cache.
     *
     * @param instancesRequestDataModel - instances request data model
     * @return classifier options result
     */
    @Locked(lockName = "getOptimalClassifierOptions", key = "#instancesRequestDataModel.dataMd5Hash")
    public ClassifierOptionsResult getOptimalClassifierOptionsFromCache(
            InstancesRequestDataModel instancesRequestDataModel) {
        String dataMd5Hash = instancesRequestDataModel.getDataMd5Hash();
        log.info("Starting to get optimal classifiers options from cache for data md5 hash: {}, options req id [{}]",
                dataMd5Hash, instancesRequestDataModel.getRequestId());
        ClassifierOptionsRequestModel requestModel = getLastClassifierOptionsRequestModel(dataMd5Hash);
        ClassifierOptionsResponseModel responseModel = getFirstResponseModel(requestModel);
        if (responseModel != null) {
            log.info(
                    "Optimal options [{}] has been taken from last response for data md5 hash '{}', options req id [{}]",
                    responseModel.getOptions(), dataMd5Hash, instancesRequestDataModel.getRequestId());
            ClassifierOptionsRequestEntity requestEntity =
                    createClassifierOptionsRequestEntity(ClassifierOptionsRequestSource.CACHE);
            requestEntity.setRequestId(instancesRequestDataModel.getRequestId());
            requestEntity.setClassifierOptionsRequestModel(requestModel);
            classifierOptionsRequestRepository.save(requestEntity);
            ClassifierOptionsResult classifierOptionsResult = new ClassifierOptionsResult();
            classifierOptionsResult.setOptionsJson(responseModel.getOptions());
            classifierOptionsResult.setFound(true);
            return classifierOptionsResult;
        } else {
            return getOptimalClassifierOptionsFromErs(instancesRequestDataModel);
        }
    }

    private ClassifierOptionsRequestModel getLastClassifierOptionsRequestModel(String dataMd5Hash) {
        List<ClassifierOptionsRequestEntity> requestModels =
                classifierOptionsRequestRepository.findLastRequests(dataMd5Hash,
                        Collections.singletonList(ErsResponseStatus.SUCCESS),
                        LocalDateTime.now().minusDays(ersConfig.getClassifierOptionsCacheDurationInDays()),
                        PageRequest.of(0, 1));
        return requestModels.stream()
                .findFirst()
                .map(ClassifierOptionsRequestEntity::getClassifierOptionsRequestModel)
                .orElse(null);
    }
}
