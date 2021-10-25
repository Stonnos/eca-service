package com.ecaservice.core.mail.client.repository;

import com.ecaservice.core.mail.client.entity.EmailRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EmailRequestEntity} entity.
 *
 * @author Roman Batygin
 */
public interface EmailRequestRepository extends JpaRepository<EmailRequestEntity, Long> {
}
