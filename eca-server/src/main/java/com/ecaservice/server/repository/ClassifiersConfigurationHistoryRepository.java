package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Implements repository to manage with {@link ClassifiersConfigurationHistoryEntity} entity.
 *
 * @author Roman Batygin
 */
public interface ClassifiersConfigurationHistoryRepository
        extends JpaRepository<ClassifiersConfigurationHistoryEntity, Long> {
}
