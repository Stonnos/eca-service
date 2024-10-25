package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.InstancesInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Service to manage with {@link InstancesInfo} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesInfoRepository
        extends JpaRepository<InstancesInfo, Long>, JpaSpecificationExecutor<InstancesInfo> {

    /**
     * Finds instances info by uuid.
     *
     * @param uuid - instances uuid
     * @return instances info
     */
    InstancesInfo findByUuid(String uuid);

    /**
     * Gets instances info list by ids.
     *
     * @param ids - ids list
     * @return instances info list
     */
    @Query("select ins.relationName from InstancesInfo ins where ins.id in (:ids)")
    List<String> getInstancesNames(@Param("ids") Collection<Long> ids);
}
