package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Classifiers configuration mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifiersConfigurationMapper {

    /**
     * Maps create classifiers configuration dto to its entity model.
     *
     * @param configurationDto - create classifiers configuration dto
     * @return classifiers configuration entity
     */
    @Mapping(target = "source", constant = "MANUAL")
    ClassifiersConfiguration map(CreateClassifiersConfigurationDto configurationDto);

    /**
     * Updates classifiers configuration entity.
     *
     * @param configurationDto         - update classifiers configuration dto
     * @param classifiersConfiguration - classifiers configuration entity
     */
    @Mapping(target = "id", ignore = true)
    void update(UpdateClassifiersConfigurationDto configurationDto,
                @MappingTarget ClassifiersConfiguration classifiersConfiguration);
}
