package com.ecaservice.data.storage.repository;

import com.ecaservice.data.storage.entity.ExportInstancesObjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

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

    /**
     * Deletes all export instances objects with specified instances uuid.
     *
     * @param instancesUuid - instances uuid
     */
    void deleteByInstancesUuid(String instancesUuid);
}
