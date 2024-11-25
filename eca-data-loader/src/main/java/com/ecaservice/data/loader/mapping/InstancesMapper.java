package com.ecaservice.data.loader.mapping;

import com.ecaservice.data.loader.dto.AttributeInfo;
import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import com.ecaservice.data.loader.entity.InstancesEntity;
import eca.data.file.model.AttributeModel;
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
     * Maps instances entity to instances meta data info dto.
     *
     * @param instancesEntity - instances entity
     * @return instances meta data info dto
     */
    InstancesMetaInfoDto map(InstancesEntity instancesEntity);

    /**
     * Maps attribute model to attribute info.
     *
     * @param attributeModel - attribute model
     * @return attribute info
     */
    AttributeInfo map(AttributeModel attributeModel);

    /**
     * Maps attribute models to attribute info list.
     *
     * @param attributeModels - attribute models list
     * @return attributes info list
     */
    List<AttributeInfo> map(List<AttributeModel> attributeModels);
}
