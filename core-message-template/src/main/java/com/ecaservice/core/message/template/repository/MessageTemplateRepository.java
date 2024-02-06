package com.ecaservice.core.message.template.repository;

import com.ecaservice.core.message.template.entity.MessageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service to manage with {@link MessageTemplateEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface MessageTemplateRepository extends JpaRepository<MessageTemplateEntity, String> {
}
