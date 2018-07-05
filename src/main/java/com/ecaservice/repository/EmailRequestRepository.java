package com.ecaservice.repository;

import com.ecaservice.model.entity.EmailRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link EmailRequestEntity} persistence entity.
 */
public interface EmailRequestRepository extends JpaRepository<EmailRequestEntity, Long> {
}
