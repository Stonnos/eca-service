package com.ecaservice.mapping;

import com.ecaservice.model.entity.ClassifierInputOptions;
import com.ecaservice.web.dto.model.InputOptionDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Evaluation log classifier input options mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface ClassifierInputOptionsMapper {

    /**
     * Maps classifier input options entity to its dto model.
     *
     * @param classifierInputOptions - classifier input options entity
     * @return classifier input options dto
     */
    InputOptionDto map(ClassifierInputOptions classifierInputOptions);

    /**
     * Maps classifier input options entities list to its dto models list.
     *
     * @param classifierInputOptionsList - classifier input options entities list
     * @return classifier input options dto list
     */
    List<InputOptionDto> map(List<ClassifierInputOptions> classifierInputOptionsList);

}
