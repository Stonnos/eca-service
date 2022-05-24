package com.ecaservice.oauth.repository;

import com.ecaservice.oauth.entity.TfaCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link TfaCodeEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface TfaCodeRepository extends JpaRepository<TfaCodeEntity, Long> {
}
