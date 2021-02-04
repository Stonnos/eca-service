package com.ecaservice.mail.repository;

import com.ecaservice.mail.model.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link TemplateEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long> {
}
