package com.ecaservice.data.loader.mapping;

import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import com.ecaservice.data.loader.entity.InstancesEntity;
import org.mapstruct.Mapper;

/**
 * Instances mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface InstancesMapper {

    /**
     * Maps instances entity to instances meta data info dto.
     *
     * @param instancesEntity - instances entity
     * @return instances meta data info dto
     */
    InstancesMetaInfoDto map(InstancesEntity instancesEntity);
}
