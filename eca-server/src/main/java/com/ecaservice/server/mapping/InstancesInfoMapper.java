package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import org.mapstruct.Mapper;

/**
 * Implements mapping instances info entity to its dto model.
 */
@Mapper
public interface InstancesInfoMapper {

    /**
     * Maps instances info to its dto model.
     *
     * @param instancesInfo - instances info entity
     * @return instances info dto
     */
    InstancesInfoDto map(InstancesInfo instancesInfo);

    /**
     * Maps instances info to instances report.
     * @param instancesInfo - instances info
     * @return instances report
     */
    InstancesReport mapToReport(InstancesInfo instancesInfo);
}
