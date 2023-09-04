package com.ecaservice.data.storage.repository;

import com.ecaservice.data.storage.entity.ExportInstancesObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link ExportInstancesObjectEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface ExportInstancesObjectRepository extends JpaRepository<ExportInstancesObjectEntity, Long> {

    /**
     * Finds last export instances object entity.
     *
     * @param instancesUuid - instances uuid
     * @return export instances object entity
     */
    ExportInstancesObjectEntity findFirstByInstancesUuidOrderByCreatedDesc(String instancesUuid);
}
