package com.ecaservice.repository;

import com.ecaservice.model.entity.AppInstanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link AppInstanceEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AppInstanceRepository extends JpaRepository<AppInstanceEntity, Long> {

    /**
     * Finds app instance by name.
     *
     * @param instanceName - instance name
     * @return app instance entity
     */
    AppInstanceEntity findByInstanceName(String instanceName);
}
