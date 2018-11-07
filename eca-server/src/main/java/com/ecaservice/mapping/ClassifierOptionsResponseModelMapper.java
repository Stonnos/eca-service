package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifierOptionsResponseModel;
import com.ecaservice.web.dto.ClassifierOptionsResponseDto;
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
     * Maps classifier options response models to its dto models.
     *
     * @param classifierOptionsResponseModels - classifier options response models entities
     * @return classifier options response dto list
     */
    List<ClassifierOptionsResponseDto> map(List<ClassifierOptionsResponseModel> classifierOptionsResponseModels);
}
