package com.ecaservice.data.loader.repository;

import com.ecaservice.data.loader.entity.InstancesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository to manage with {@link InstancesEntity} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesRepository extends JpaRepository<InstancesEntity, Long> {

    /**
     * Finds instances entity by uuid.
     *
     * @param uuid - instances uuid
     * @return instances
     */
    Optional<InstancesEntity> findByUuid(String uuid);

    /**
     * Finds instances by md5 hash.
     *
     * @param md5Hash  - md5 hash
     * @return instances entity
     */
    InstancesEntity findByMd5Hash(String md5Hash);

    /**
     * Finds instances page by ids.
     *
     * @param ids - ids list
     * @return instances page
     */
    List<InstancesEntity> findByIdIn(Collection<Long> ids);
}
