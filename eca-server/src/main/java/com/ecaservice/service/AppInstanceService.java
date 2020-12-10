package com.ecaservice.service;

import com.ecaservice.config.CommonConfig;
import com.ecaservice.model.entity.AppInstanceEntity;
import com.ecaservice.repository.AppInstanceRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    @Getter
    private AppInstanceEntity appInstanceEntity;

    /**
     * Loads app instance or save new if not exists.
     */
    @PostConstruct
    public void saveAppInstance() {
        log.info("Starting to load app instance info");
        appInstanceEntity = appInstanceRepository.findByInstanceName(commonConfig.getInstance());
        if (appInstanceEntity == null) {
            appInstanceEntity = new AppInstanceEntity();
            appInstanceEntity.setInstanceName(commonConfig.getInstance());
            appInstanceRepository.save(appInstanceEntity);
            log.info("New [{}] app instance has been registered", appInstanceEntity.getInstanceName());
        }
    }
}
