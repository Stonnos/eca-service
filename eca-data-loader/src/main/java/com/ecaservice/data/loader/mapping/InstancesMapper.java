package com.ecaservice.data.loader.mapping;

import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import com.ecaservice.data.loader.dto.InstancesMetaInfoRequestDto;
import com.ecaservice.data.loader.entity.InstancesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
    @Mapping(source = "instancesObject.md5Hash", target = "md5Hash")
    @Mapping(source = "instancesObject.objectPath", target = "objectPath")
    InstancesMetaInfoDto map(InstancesEntity instancesEntity);

    /**
     * Maps instances meta info request dto to instances entity.
     *
     * @param instancesMetaInfoRequestDto - instances meta info request dto
     * @return instances entity
     */
    InstancesEntity map(InstancesMetaInfoRequestDto instancesMetaInfoRequestDto);
}
