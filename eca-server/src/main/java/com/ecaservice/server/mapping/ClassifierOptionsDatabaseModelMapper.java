package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.server.report.model.ClassifierOptionsBean;
import com.ecaservice.web.dto.model.ClassifierOptionsDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Classifier options database model mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = DateTimeConverter.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ClassifierOptionsDatabaseModelMapper {

    /**
     * Maps classifier options database model to dto model.
     *
     * @param classifierOptionsDatabaseModel classifier options database model
     * @return classifier options dto model
     */
    ClassifierOptionsDto map(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel);

    /**
     * Maps classifier options database model to report bean model.
     *
     * @param classifierOptionsDatabaseModel classifier options database model
     * @return classifier options report bean model
     */
    @Mapping(source = "creationDate", target = "creationDate", qualifiedByName = "formatLocalDateTime")
    @Mapping(target = "optionsName", ignore = true)
    ClassifierOptionsBean mapToBean(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel);

    /**
     * Maps classifier options database models list to dto models list.
     *
     * @param classifierOptionsDatabaseModels classifier options database model
     * @return classifier options dto model
     */
    List<ClassifierOptionsDto> map(List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels);
}
