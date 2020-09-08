package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import com.ecaservice.report.model.ClassifierOptionsBean;
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
    ClassifierOptionsBean mapToBean(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel);

    /**
     * Maps classifier options database models list to report beans list.
     *
     * @param classifierOptionsDatabaseModels classifier options database model
     * @return classifier options report beans
     */
    List<ClassifierOptionsBean> mapTpBeans(List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels);

    /**
     * Maps classifier options database models list to dto models list.
     *
     * @param classifierOptionsDatabaseModels classifier options database model
     * @return classifier options dto model
     */
    List<ClassifierOptionsDto> map(List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels);
}
