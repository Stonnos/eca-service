package com.ecaservice.ers.mapping;

import com.ecaservice.ers.dto.InstancesReport;
import com.ecaservice.ers.model.InstancesInfo;
import com.ecaservice.web.dto.model.InstancesInfoDto;
import org.mapstruct.Mapper;

/**
 * Instances report mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface InstancesMapper {

    /**
     * Maps instances report to instances info entity.
     *
     * @param instancesReport - instances report entity
     * @return instances info entity
     */
    InstancesInfo map(InstancesReport instancesReport);

    /**
     * Maps instances info entity to instances report model.
     *
     * @param instancesInfo - instances info entity
     * @return instances report
     */
    InstancesReport map(InstancesInfo instancesInfo);

    /**
     * Maps instances info entity to instances info dto model.
     *
     * @param instancesInfo - instances info entity
     * @return instances report
     */
    InstancesInfoDto mapToInstancesInfoDto(InstancesInfo instancesInfo);
}
