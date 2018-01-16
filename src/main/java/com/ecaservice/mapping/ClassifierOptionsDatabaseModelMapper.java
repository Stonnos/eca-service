package com.ecaservice.mapping;

import com.ecaservice.dto.ClassifierOptionsDto;
import com.ecaservice.model.entity.ClassifierOptionsDatabaseModel;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Classifier options database model mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifierOptionsDatabaseModelMapper {

    /**
     * Maps classifier options database model to dto model.
     *
     * @param classifierOptionsDatabaseModel {@link ClassifierOptionsDatabaseModel} object
     * @return {@link ClassifierOptionsDto} object
     */
    ClassifierOptionsDto map(ClassifierOptionsDatabaseModel classifierOptionsDatabaseModel);

    /**
     * Maps classifier options database models list to dto models list.
     *
     * @param classifierOptionsDatabaseModels {@link ClassifierOptionsDatabaseModel} list
     * @return {@link ClassifierOptionsDto} list
     */
    List<ClassifierOptionsDto> map(List<ClassifierOptionsDatabaseModel> classifierOptionsDatabaseModels);
}
