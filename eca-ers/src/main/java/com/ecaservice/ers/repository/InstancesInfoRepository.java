package com.ecaservice.ers.repository;

import com.ecaservice.ers.model.InstancesInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository to manage with {@link InstancesInfo} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesInfoRepository
        extends JpaRepository<InstancesInfo, Long>, JpaSpecificationExecutor<InstancesInfo> {

    /**
     * Finds instances info by MD5 hash.
     *
     * @param dataMd5Hash - data MD5 hash
     * @return instances info entity
     */
    InstancesInfo findByDataMd5Hash(String dataMd5Hash);
}
