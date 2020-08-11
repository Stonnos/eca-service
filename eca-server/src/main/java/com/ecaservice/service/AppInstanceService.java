package com.ecaservice.service;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.config.cache.CacheNames;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.repository.AppInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * App instance service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class AppInstanceService {

    private final CommonConfig commonConfig;
    private final AppInstanceRepository appInstanceRepository;

    private final Object lifeCycleMonitor = new Object();

    /**
     * Constructor with spring dependency injection.
     *
     * @param commonConfig          - common config bean
     * @param appInstanceRepository - app instance repository bean
     */
    public AppInstanceService(CommonConfig commonConfig,
                              AppInstanceRepository appInstanceRepository) {
        this.commonConfig = commonConfig;
        this.appInstanceRepository = appInstanceRepository;
    }

    /**
     * Gets current app instance entity or save new if not exists.
     *
     * @return current app instance entity
     */
    @Cacheable(CacheNames.APP_INSTANCE_CACHE_NAME)
    public AppInstanceEntity getOrSaveAppInstance() {
        AppInstanceEntity appInstanceEntity = appInstanceRepository.findByInstanceName(commonConfig.getInstance());
        if (appInstanceEntity == null) {
            //Double check app instance in synchronized block for performance improving
            synchronized (lifeCycleMonitor) {
                appInstanceEntity = appInstanceRepository.findByInstanceName(commonConfig.getInstance());
                if (appInstanceEntity == null) {
                    appInstanceEntity = new AppInstanceEntity();
                    appInstanceEntity.setInstanceName(commonConfig.getInstance());
                    appInstanceRepository.save(appInstanceEntity);
                }
            }
        }
        return appInstanceEntity;
    }
}
