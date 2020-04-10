package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

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

    /**
     * Maps classifiers configuration entity to its dto model.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     * @return classifiers configuration dto
     */
    ClassifiersConfigurationDto map(ClassifiersConfiguration classifiersConfiguration);

    /**
     * Maps classifiers configuration entities to its dto models.
     *
     * @param classifiersConfiguration - classifiers configuration entities
     * @return classifiers configurations dto list
     */
    List<ClassifiersConfigurationDto> map(List<ClassifiersConfiguration> classifiersConfiguration);
}
