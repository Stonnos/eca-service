package com.ecaservice.repository;

import com.ecaservice.model.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Implements repository that deals with {@link Email} entities.
 * @author Roman Batygin
 */
public interface EmailRepository extends JpaRepository<Email, Long> {
}
