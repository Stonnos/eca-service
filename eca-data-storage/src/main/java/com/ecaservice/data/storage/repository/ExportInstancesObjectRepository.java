package com.ecaservice.data.storage.repository;

import com.ecaservice.data.storage.entity.ExportInstancesObjectEntity;
import com.ecaservice.data.storage.entity.InstancesEntity;
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
     * @param instancesEntity - instances entity
     * @return export instances object entity
     */
    ExportInstancesObjectEntity findFirstByInstancesEntityOrderByCreatedDesc(InstancesEntity instancesEntity);

    /**
     * Finds export instances object entity by external uuid.
     *
     * @param externalDataUuid - external data uuid in central data storage
     * @return export instances object entity
     */
    ExportInstancesObjectEntity findFirstByExternalDataUuid(String externalDataUuid);
}
