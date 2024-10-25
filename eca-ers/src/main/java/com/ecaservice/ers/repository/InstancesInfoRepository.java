package com.ecaservice.ers.repository;

import com.ecaservice.ers.model.InstancesInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Repository to manage with {@link InstancesInfo} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesInfoRepository
        extends JpaRepository<InstancesInfo, Long>, JpaSpecificationExecutor<InstancesInfo> {

    /**
     * Finds instances info by uuid.
     *
     * @param uuid - data uuid
     * @return instances info entity
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
