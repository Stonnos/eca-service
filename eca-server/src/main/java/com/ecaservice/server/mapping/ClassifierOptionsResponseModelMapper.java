package com.ecaservice.server.mapping;

import com.ecaservice.server.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.web.dto.model.ClassifierOptionsResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Implements mapping classifier options response model to its dto model.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifierOptionsResponseModelMapper {

    /**
     * Maps classifier options response model to its dto model.
     *
     * @param classifierOptionsResponseModel - classifier options response model entity
     * @return classifier options response dto
     */
    ClassifierOptionsResponseDto map(ClassifierOptionsResponseModel classifierOptionsResponseModel);

    /**
     * Maps classifiers options responses models to its dto models.
     *
     * @param classifierOptionsResponseModels - classifiers options responses models entities
     * @return classifiers options responses dto list
     */
    List<ClassifierOptionsResponseDto> map(List<ClassifierOptionsResponseModel> classifierOptionsResponseModels);
}
