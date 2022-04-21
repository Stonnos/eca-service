package com.ecaservice.core.form.template.repository;

import com.ecaservice.core.form.template.entity.FormTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Implements repository that manages with {@link FormTemplateEntity} entities.
 *
 * @author Roman Batygin
 */
public interface FormTemplateRepository extends JpaRepository<FormTemplateEntity, Long> {

    /**
     * Finds form template by class.
     *
     * @param objectClass - object class
     * @return form template entity
     */
    Optional<FormTemplateEntity> findByObjectClass(String objectClass);
}
