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
     * Finds instances info by md5 hash.
     *
     * @param dataMd5Hash - data md5 hash
     * @return instances info
     */
    InstancesInfo findByDataMd5Hash(String dataMd5Hash);

    /**
     * Gets instances info list by ids.
     *
     * @param ids - ids list
     * @return instances info list
     */
    @Query("select ins.relationName from InstancesInfo ins where ins.id in (:ids)")
    List<String> getInstancesNames(@Param("ids") Collection<Long> ids);
}
