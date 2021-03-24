package com.ecaservice.service.evaluation;

import com.ecaservice.aspect.annotation.Locked;
import com.ecaservice.config.ers.ErsConfig;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.model.entity.ErsResponseStatus;
import com.ecaservice.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.repository.ClassifierOptionsRequestRepository;
import com.ecaservice.service.AppInstanceService;
import com.ecaservice.service.ers.ErsRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.util.Utils.createClassifierOptionsRequestEntity;
import static com.ecaservice.util.Utils.getFirstResponseModel;

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
    private final ErsRequestService ersRequestService;
    private final AppInstanceService appInstanceService;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ClassifierOptionsRequestRepository classifierOptionsRequestRepository;

    /**
     * Gets optimal classifiers options from ERS service.
     *
     * @param classifierOptionsRequest - classifier options request
     * @param dataMd5Hash              - data MD5 hash
     * @return classifier options string
     */
    public String getOptimalClassifierOptionsFromErs(ClassifierOptionsRequest classifierOptionsRequest,
                                                     String dataMd5Hash) {
        log.info("Starting to get optimal classifiers options from ERS for data: {}",
                classifierOptionsRequest.getInstances().getRelationName());
        ClassifierOptionsRequestEntity requestEntity =
                createClassifierOptionsRequestEntity(ClassifierOptionsRequestSource.ERS);
        ClassifierOptionsRequestModel requestModel =
                createClassifierOptionsRequestModel(classifierOptionsRequest, dataMd5Hash);
        String options = ersRequestService.getOptimalClassifierOptions(classifierOptionsRequest, requestModel);
        requestEntity.setClassifierOptionsRequestModel(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        return options;
    }

    /**
     * Gets optimal classifiers options from cache.
     *
     * @param classifierOptionsRequest - classifier options request
     * @param dataMd5Hash              - data MD5 hash
     * @return classifier options string
     */
    @Locked(lockName = "getOptimalClassifierOptions", key = "#dataMd5Hash")
    public String getOptimalClassifierOptionsFromCache(ClassifierOptionsRequest classifierOptionsRequest,
                                                       String dataMd5Hash) {
        log.info("Starting to get optimal classifiers options from cache for data: {}",
                classifierOptionsRequest.getInstances().getRelationName());
        String options;
        ClassifierOptionsRequestModel requestModel = getLastClassifierOptionsRequestModel(dataMd5Hash);
        ClassifierOptionsResponseModel responseModel = getFirstResponseModel(requestModel);
        if (responseModel != null) {
            log.info("Optimal classifier options [{}] has been taken from last response for data '{}'.",
                    responseModel.getOptions(), classifierOptionsRequest.getInstances().getRelationName());
            ClassifierOptionsRequestEntity requestEntity =
                    createClassifierOptionsRequestEntity(ClassifierOptionsRequestSource.CACHE);
            requestEntity.setClassifierOptionsRequestModel(requestModel);
            classifierOptionsRequestRepository.save(requestEntity);
            options = responseModel.getOptions();
        } else {
            options = getOptimalClassifierOptionsFromErs(classifierOptionsRequest, dataMd5Hash);
        }
        return options;
    }

    private ClassifierOptionsRequestModel createClassifierOptionsRequestModel(
            ClassifierOptionsRequest classifierOptionsRequest, String dataMd5Hash) {
        AppInstanceEntity appInstanceEntity = appInstanceService.getAppInstanceEntity();
        ClassifierOptionsRequestModel requestModel =
                classifierOptionsRequestModelMapper.map(classifierOptionsRequest);
        requestModel.setRelationName(classifierOptionsRequest.getInstances().getRelationName());
        requestModel.setDataMd5Hash(dataMd5Hash);
        requestModel.setAppInstanceEntity(appInstanceEntity);
        return requestModel;
    }

    private ClassifierOptionsRequestModel getLastClassifierOptionsRequestModel(String dataMd5Hash) {
        List<ClassifierOptionsRequestEntity> requestModels =
                classifierOptionsRequestRepository.findLastRequests(dataMd5Hash,
                        Collections.singletonList(ErsResponseStatus.SUCCESS),
                        LocalDateTime.now().minusDays(ersConfig.getClassifierOptionsCacheDurationInDays()),
                        PageRequest.of(0, 1));
        return requestModels.stream().findFirst().map(
                ClassifierOptionsRequestEntity::getClassifierOptionsRequestModel).orElse(null);
    }
}
