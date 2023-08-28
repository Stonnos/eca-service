package com.ecaservice.data.loader.repository;

import com.ecaservice.data.loader.entity.InstancesObject;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link InstancesObject} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesObjectRepository extends JpaRepository<InstancesObject, Long> {
}
