package com.ecaservice.data.storage.repository;

import com.ecaservice.data.storage.entity.AttributeValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link AttributeValueEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AttributeValueRepository extends JpaRepository<AttributeValueEntity, Long> {
}
