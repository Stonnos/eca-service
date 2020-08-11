package com.ecaservice.service;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.repository.AppInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * App instance service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppInstanceService {

    private final CommonConfig commonConfig;
    private final AppInstanceRepository appInstanceRepository;

    /**
     * Gets current app instance entity or save new if not exists.
     *
     * @return current app instance entity
     */
    public AppInstanceEntity getOrSaveAppInstance() {
        AppInstanceEntity appInstanceEntity = appInstanceRepository.findByInstanceName(commonConfig.getInstance());
        if (appInstanceEntity == null) {
            appInstanceEntity = new AppInstanceEntity();
            appInstanceEntity.setInstanceName(commonConfig.getInstance());
            appInstanceRepository.save(appInstanceEntity);
        }
        return appInstanceEntity;
    }
}
