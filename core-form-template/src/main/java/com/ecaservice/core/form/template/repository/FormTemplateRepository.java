package com.ecaservice.core.form.template.repository;

import com.ecaservice.core.form.template.entity.FormTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Implements repository that manages with {@link FormTemplateEntity} entities.
 *
 * @author Roman Batygin
 */
public interface FormTemplateRepository extends JpaRepository<FormTemplateEntity, Long> {
}
