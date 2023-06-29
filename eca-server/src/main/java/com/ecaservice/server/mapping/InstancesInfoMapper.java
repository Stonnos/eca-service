package com.ecaservice.server.mapping;

import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.server.model.entity.InstancesInfo;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import org.mapstruct.Mapper;

import java.util.List;

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
     * Maps instances info list to dto models list.
     *
     * @param instancesInfoList - instances info entities list
     * @return instances info dto list
     */
    List<InstancesInfoDto> map(List<InstancesInfo> instancesInfoList);

    /**
     * Maps instances info to instances report.
     * @param instancesInfo - instances info
     * @return instances report
     */
    InstancesReport mapToReport(InstancesInfo instancesInfo);
}
