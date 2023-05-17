package com.ecaservice.data.storage.repository;

import com.ecaservice.data.storage.entity.AttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link AttributeEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AttributeRepository extends JpaRepository<AttributeEntity, Long> {
}
