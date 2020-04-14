package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifiersConfiguration;
import com.ecaservice.web.dto.model.ClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.CreateClassifiersConfigurationDto;
import com.ecaservice.web.dto.model.UpdateClassifiersConfigurationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;

/**
 * Classifiers configuration mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public abstract class ClassifiersConfigurationMapper {

    /**
     * Maps create classifiers configuration dto to its entity model.
     *
     * @param configurationDto - create classifiers configuration dto
     * @return classifiers configuration entity
     */
    @Mapping(source = "name", target = "name", qualifiedByName = "trim")
    public abstract ClassifiersConfiguration map(CreateClassifiersConfigurationDto configurationDto);

    /**
     * Updates classifiers configuration entity.
     *
     * @param configurationDto         - update classifiers configuration dto
     * @param classifiersConfiguration - classifiers configuration entity
     */
    @Mapping(source = "name", target = "name", qualifiedByName = "trim")
    @Mapping(target = "id", ignore = true)
    public abstract void update(UpdateClassifiersConfigurationDto configurationDto,
                @MappingTarget ClassifiersConfiguration classifiersConfiguration);

    /**
     * Maps classifiers configuration entity to its dto model.
     *
     * @param classifiersConfiguration - classifiers configuration entity
     * @return classifiers configuration dto
     */
    public abstract ClassifiersConfigurationDto map(ClassifiersConfiguration classifiersConfiguration);

    /**
     * Maps classifiers configuration entities to its dto models.
     *
     * @param classifiersConfiguration - classifiers configuration entities
     * @return classifiers configurations dto list
     */
    public abstract List<ClassifiersConfigurationDto> map(List<ClassifiersConfiguration> classifiersConfiguration);

    @Named("trim")
    protected String trim(String value) {
        return Optional.ofNullable(value).map(String::trim).orElse(null);
    }
}
