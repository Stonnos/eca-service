package com.ecaservice.data.storage.mapping;

import com.ecaservice.data.storage.entity.AttributeEntity;
import com.ecaservice.data.storage.entity.AttributeValueEntity;
import com.ecaservice.web.dto.model.AttributeDto;
import com.ecaservice.web.dto.model.AttributeValueDto;
import com.ecaservice.web.dto.model.EnumDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Attribute mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface AttributeMapper {

    /**
     * Maps attribute entity to dto model.
     *
     * @param attributeEntity - attribute entity
     * @return attribute dto
     */
    @Mapping(source = "columnName", target = "name")
    @Mapping(target = "type", ignore = true)
    AttributeDto map(AttributeEntity attributeEntity);

    /**
     * Maps attribute entities list to dto models list.
     *
     * @param attributeEntityList - attribute entities list
     * @return attribute dto list
     */
    List<AttributeDto> map(List<AttributeEntity> attributeEntityList);

    /**
     * Maps attribute value entity to dto model.
     *
     * @param attributeValueEntity - attribute value entity
     * @return attribute dto
     */
    AttributeValueDto map(AttributeValueEntity attributeValueEntity);

    /**
     * Maps attribute value entities list to dto models list.
     *
     * @param attributeValueEntities - attribute value entities list
     * @return attribute value dto list
     */
    List<AttributeValueDto> mapValues(List<AttributeValueEntity> attributeValueEntities);

    /**
     * Maps attribute type.
     *
     * @param attributeEntity - attribute entity
     * @param attributeDto    - target attribute dto
     */
    @AfterMapping
    default void mapType(AttributeEntity attributeEntity, @MappingTarget AttributeDto attributeDto) {
        attributeDto.setType(new EnumDto(attributeEntity.getType().name(), attributeEntity.getType().getDescription()));
    }
}
