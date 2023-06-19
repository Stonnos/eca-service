package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.mapping.ClassifierOptionsRequestModelMapper;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.server.repository.ClassifierOptionsRequestRepository;
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
    private final ErsRequestService ersRequestService;
    private final ClassifierOptionsRequestModelMapper classifierOptionsRequestModelMapper;
    private final ClassifierOptionsRequestRepository classifierOptionsRequestRepository;

    /**
     * Gets optimal classifiers options from ERS service.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options
     */
    public ClassifierOptionsResult getOptimalClassifierOptionsFromErs(
            ClassifierOptionsRequest classifierOptionsRequest) {
        log.info("Starting to get optimal classifiers options from ERS for data md5 hash: {}",
                classifierOptionsRequest.getDataHash());
        ClassifierOptionsRequestEntity requestEntity =
                createClassifierOptionsRequestEntity(ClassifierOptionsRequestSource.ERS);
        requestEntity.setRequestId(classifierOptionsRequest.getRequestId());
        ClassifierOptionsRequestModel requestModel = classifierOptionsRequestModelMapper.map(classifierOptionsRequest);
        ClassifierOptionsResult classifierOptionsResult =
                ersRequestService.getOptimalClassifierOptions(classifierOptionsRequest, requestModel);
        requestEntity.setClassifierOptionsRequestModel(requestModel);
        classifierOptionsRequestRepository.save(requestEntity);
        return classifierOptionsResult;
    }

    /**
     * Gets optimal classifiers options from cache.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options string
     */
    @Locked(lockName = "getOptimalClassifierOptions", key = "#classifierOptionsRequest.dataMd5Hash")
    public ClassifierOptionsResult getOptimalClassifierOptionsFromCache(
            ClassifierOptionsRequest classifierOptionsRequest) {
        String dataMd5Hash = classifierOptionsRequest.getDataHash();
        log.info("Starting to get optimal classifiers options from cache for data md5 hash: {}",
                classifierOptionsRequest.getDataHash());
        ClassifierOptionsRequestModel requestModel = getLastClassifierOptionsRequestModel(dataMd5Hash);
        ClassifierOptionsResponseModel responseModel = getFirstResponseModel(requestModel);
        if (responseModel != null) {
            log.info("Optimal classifier options [{}] has been taken from last response for data md5 hash '{}'.",
                    responseModel.getOptions(), classifierOptionsRequest.getDataHash());
            ClassifierOptionsRequestEntity requestEntity =
                    createClassifierOptionsRequestEntity(ClassifierOptionsRequestSource.CACHE);
            requestEntity.setRequestId(classifierOptionsRequest.getRequestId());
            requestEntity.setClassifierOptionsRequestModel(requestModel);
            classifierOptionsRequestRepository.save(requestEntity);
            ClassifierOptionsResult classifierOptionsResult = new ClassifierOptionsResult();
            classifierOptionsResult.setOptionsJson(responseModel.getOptions());
            classifierOptionsResult.setFound(true);
            return classifierOptionsResult;
        } else {
            return getOptimalClassifierOptionsFromErs(classifierOptionsRequest);
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
