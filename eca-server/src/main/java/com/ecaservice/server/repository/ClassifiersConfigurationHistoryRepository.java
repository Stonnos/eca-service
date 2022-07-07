package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.ClassifiersConfiguration;
import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Implements repository to manage with {@link ClassifiersConfigurationHistoryEntity} entity.
 *
 * @author Roman Batygin
 */
public interface ClassifiersConfigurationHistoryRepository extends JpaRepository<ClassifiersConfigurationHistoryEntity, Long>, JpaSpecificationExecutor<ClassifiersConfigurationHistoryEntity> {

    /**
     * Gets classifiers configuration history page.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     * @param specification            - specification object
     * @param pageable                 - pageable object
     * @return classifiers configuration history page
     */
    Page<ClassifiersConfigurationHistoryEntity> findAllByConfiguration(
            ClassifiersConfiguration classifiersConfiguration,
            Specification<ClassifiersConfigurationHistoryEntity> specification,
            Pageable pageable);
}
