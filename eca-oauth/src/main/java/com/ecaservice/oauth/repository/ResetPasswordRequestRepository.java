package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.ResetPasswordRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ResetPasswordRequest} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ResetPasswordRequestRepository extends JpaRepository<ResetPasswordRequest, Long> {
}
