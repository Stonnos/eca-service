package com.ecaservice.core.form.template.repository;

import com.ecaservice.core.form.template.entity.FormTemplateGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Implements repository that manages with {@link FormTemplateGroupEntity} entities.
 *
 * @author Roman Batygin
 */
public interface FormTemplateGroupRepository extends JpaRepository<FormTemplateGroupEntity, Long> {

    /**
     * Finds form templates group by name.
     *
     * @param groupName - group name
     * @return form templates group entity
     */
    Optional<FormTemplateGroupEntity> findByGroupName(String groupName);
}
