package com.ecaservice.external.api.repository;

import com.ecaservice.external.api.entity.InstancesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
     * Finds not deleted instances ids.
     *
     * @param dateTime - date time threshold value
     * @return instances ids
     */
    @Query("select ins.id from InstancesEntity ins where ins.creationDate < :dateTime order by ins.creationDate")
    List<Long> findNotDeletedData(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Finds instances page by ids.
     *
     * @param ids - ids list
     * @return instances page
     */
    List<InstancesEntity> findByIdIn(Collection<Long> ids);
}
