package com.ecaservice.data.storage.repository;

import com.ecaservice.data.storage.entity.InstancesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Repository to manage with {@link InstancesEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesRepository
        extends JpaRepository<InstancesEntity, Long>, JpaSpecificationExecutor<InstancesEntity> {

    /**
     * Checks instances exists with specified table name.
     *
     * @param tableName - table name
     * @return {@code true} if instances exists, otherwise {@code  false}
     */
    boolean existsByTableName(String tableName);

    /**
     * Finds instances entity by uuid.
     *
     * @param uuid - uuid
     * @return instances entity
     */
    Optional<InstancesEntity> findByUuid(String uuid);
}
