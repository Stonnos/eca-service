package com.ecaservice.data.storage.mapping;

import com.ecaservice.data.storage.entity.InstancesEntity;
import com.ecaservice.web.dto.model.InstancesDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Instances mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface InstancesMapper {

    /**
     * Maps instances entity to dto model.
     *
     * @param instancesEntity - instances entity
     * @return instances dto
     */
    InstancesDto map(InstancesEntity instancesEntity);

    /**
     * Maps instances entities list to dto models list.
     *
     * @param instancesEntityList - instances entities list
     * @return instances dto list
     */
    List<InstancesDto> map(List<InstancesEntity> instancesEntityList);
}
