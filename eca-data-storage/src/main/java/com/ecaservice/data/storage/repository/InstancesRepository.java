package com.ecaservice.data.storage.repository;

import com.ecaservice.data.storage.entity.InstancesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository to manage with {@link InstancesEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesRepository
        extends JpaRepository<InstancesEntity, Long>, JpaSpecificationExecutor<InstancesEntity> {

    /**
     * Checks existing with specified table name.
     *
     * @param tableName - table name
     * @return {@code true} if table with specified name exists, otherwise {@code false}
     */
    boolean existsByTableName(String tableName);
}
