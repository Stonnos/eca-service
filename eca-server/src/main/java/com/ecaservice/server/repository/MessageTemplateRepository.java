package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.MessageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service to manage with {@link MessageTemplateEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface MessageTemplateRepository extends JpaRepository<MessageTemplateEntity, String> {
}
