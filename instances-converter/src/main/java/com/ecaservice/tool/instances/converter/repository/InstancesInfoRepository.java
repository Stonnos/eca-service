package com.ecaservice.tool.instances.converter.repository;

import com.ecaservice.tool.instances.converter.entity.InstancesInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository to manage with {@link InstancesInfo} persistence entity.
 *
 * @author Roman Batygin
 */
public interface InstancesInfoRepository extends JpaRepository<InstancesInfo, Long> {
}
