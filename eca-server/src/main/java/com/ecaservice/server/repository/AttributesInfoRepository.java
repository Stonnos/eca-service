package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.AttributesInfoEntity;
import com.ecaservice.server.model.entity.InstancesInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Service to manage with {@link AttributesInfoEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface AttributesInfoRepository extends JpaRepository<AttributesInfoEntity, Long> {

    /**
     * Gets attributes info for specified instances.
     *
     * @param instancesInfo - instances info
     * @return attributes info
     */
    Optional<AttributesInfoEntity> findByInstancesInfo(InstancesInfo instancesInfo);
}
