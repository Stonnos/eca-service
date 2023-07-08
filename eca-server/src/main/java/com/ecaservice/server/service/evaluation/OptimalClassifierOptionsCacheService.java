package com.ecaservice.server.service.evaluation;

import com.ecaservice.core.lock.annotation.Locked;
import com.ecaservice.server.config.ers.ErsConfig;
import com.ecaservice.server.model.ClassifierOptionsResult;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestEntity;
import com.ecaservice.server.model.entity.ClassifierOptionsRequestModel;
import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.server.model.entity.ErsResponseStatus;
import com.ecaservice.server.model.evaluation.ClassifierOptionsRequestSource;
import com.ecaservice.server.model.evaluation.InstancesRequestDataModel;
import com.ecaservice.server.repository.ClassifierOptionsRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.server.util.Utils.createClassifierOptionsRequestEntity;
import static com.ecaservice.server.util.Utils.getFirstResponseModel;

/**
 * Optimal classifier options cache service proxy class.
 *
 * @author Roman Batygin
 */
@Slf4j
@ConditionalOnProperty(value = "ers.useClassifierOptionsCache", havingValue = "true")
@Primary
@Service
public class OptimalClassifierOptionsCacheService implements OptimalClassifierOptionsFetcher {

    private final ErsConfig ersConfig;
    private final OptimalClassifierOptionsFetcher optimalClassifierOptionsFetcher;
    private final ClassifierOptionsRequestRepository classifierOptionsRequestRepository;

    /**
     * Constructor with parameters.
     *
     * @param ersConfig                          - ers config bean
     * @param optimalClassifierOptionsFetcher    - optimal classifier options fetcher
     * @param classifierOptionsRequestRepository - classifier options request repository
     */
    public OptimalClassifierOptionsCacheService(ErsConfig ersConfig,
                                                @Qualifier("optimalClassifierOptionsFetcherImpl")
                                                OptimalClassifierOptionsFetcher optimalClassifierOptionsFetcher,
                                                ClassifierOptionsRequestRepository classifierOptionsRequestRepository) {
        this.ersConfig = ersConfig;
        this.optimalClassifierOptionsFetcher = optimalClassifierOptionsFetcher;
        this.classifierOptionsRequestRepository = classifierOptionsRequestRepository;
    }

    /**
     * Init method.
     */
    @PostConstruct
    public void init() {
        log.info("Optimal classifiers options cache service has been initialized");
    }

    /**
     * Gets optimal classifiers options from cache.
     *
     * @param instancesRequestDataModel - instances request data model
     * @return classifier options result
     */
    @Override
    @Locked(lockName = "getOptimalClassifierOptions", key = "#instancesRequestDataModel.dataMd5Hash")
    public ClassifierOptionsResult getOptimalClassifierOptions(InstancesRequestDataModel instancesRequestDataModel) {
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
            return optimalClassifierOptionsFetcher.getOptimalClassifierOptions(instancesRequestDataModel);
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
