package com.ecaservice.server.repository;

import com.ecaservice.server.model.entity.InstancesInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Service to manage with {@link InstancesInfo} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesInfoRepository
        extends JpaRepository<InstancesInfo, String>, JpaSpecificationExecutor<InstancesInfo> {

    /**
     * Finds instances info by md5 hash.
     *
     * @param dataMd5Hash - data md5 hash
     * @return instances info
     */
    InstancesInfo findByDataMd5Hash(String dataMd5Hash);
}
