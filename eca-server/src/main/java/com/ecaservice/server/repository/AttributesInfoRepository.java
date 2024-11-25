package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.AttributesInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Service to manage with {@link AttributesInfoEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AttributesInfoRepository extends JpaRepository<AttributesInfoEntity, Long> {
}
