package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.ClassifiersConfigurationHistoryEntity;
import com.ecaservice.web.dto.model.ClassifiersConfigurationHistoryDto;
import com.ecaservice.web.dto.model.EnumDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Classifiers configuration history mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifiersConfigurationHistoryMapper {

    /**
     * Maps classifiers configuration history entity to dto model.
     *
     * @param configurationHistoryEntity - classifiers configuration history entity
     * @return classifiers configuration history dto
     */
    @Mapping(target = "actionType", ignore = true)
    ClassifiersConfigurationHistoryDto map(ClassifiersConfigurationHistoryEntity configurationHistoryEntity);

    /**
     * Maps classifiers configuration history entities to dto models.
     *
     * @param configurationHistoryEntityList - classifiers configuration history entities list
     * @return classifiers configuration history dto list
     */
    List<ClassifiersConfigurationHistoryDto> map(
            List<ClassifiersConfigurationHistoryEntity> configurationHistoryEntityList);

    /**
     * Maps action type.
     *
     * @param configurationHistoryEntity         - classifiers configuration history entity
     * @param classifiersConfigurationHistoryDto - classifiers configuration history dto
     */
    @AfterMapping
    default void mapActionType(ClassifiersConfigurationHistoryEntity configurationHistoryEntity,
                               @MappingTarget ClassifiersConfigurationHistoryDto classifiersConfigurationHistoryDto) {
        var actionTypeEnumDto = new EnumDto(configurationHistoryEntity.getActionType().name(), configurationHistoryEntity.getActionType().getDescription());
        classifiersConfigurationHistoryDto.setActionType(actionTypeEnumDto);
    }
}
