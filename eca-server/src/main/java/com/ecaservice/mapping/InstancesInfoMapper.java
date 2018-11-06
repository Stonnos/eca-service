package com.ecaservice.mapping;

import com.ecaservice.model.entity.InstancesInfo;
import com.ecaservice.web.dto.InstancesInfoDto;
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
}
