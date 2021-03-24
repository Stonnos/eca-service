package com.ecaservice.ers.repository;

import com.ecaservice.ers.model.InstancesInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository to manage with {@link InstancesInfo} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesInfoRepository extends JpaRepository<InstancesInfo, Long> {

    /**
     * Finds instances info id by MD5 hash.
     *
     * @param dataMd5Hash - data MD5 hash
     * @return instances info id
     */
    @Query("select ins.id from InstancesInfo ins where ins.dataMd5Hash = :dataMd5Hash")
    Long findIdByDataMd5Hash(@Param("dataMd5Hash") String dataMd5Hash);
}
