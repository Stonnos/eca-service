package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Implements repository to manage with {@link ClassifiersConfigurationHistoryEntity} entity.
 *
 * @author Roman Batygin
 */
public interface ClassifiersConfigurationHistoryRepository extends JpaRepository<ClassifiersConfigurationHistoryEntity, Long>, JpaSpecificationExecutor<ClassifiersConfigurationHistoryEntity> {

    /**
     * Deletes all classifiers configuration history.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     * @return deleted rows count
     */
    long deleteAllByConfiguration(ClassifiersConfiguration classifiersConfiguration);
}
