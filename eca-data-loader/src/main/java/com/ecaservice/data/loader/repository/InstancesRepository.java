package com.ecaservice.data.loader.repository;

import com.ecaservice.data.loader.entity.InstancesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository to manage with {@link InstancesEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesRepository extends JpaRepository<InstancesEntity, Long> {

    /**
     * Finds instances entity by uuid.
     *
     * @param uuid - instances uuid
     * @return instances
     */
    Optional<InstancesEntity> findByUuid(String uuid);
}