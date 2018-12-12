package com.ecaservice.mapping.filters;

import com.ecaservice.model.entity.FilterFieldValue;
import com.ecaservice.web.dto.model.FilterFieldValueDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Filter field value mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface FilterFieldValueMapper {

    /**
     * Maps filter field value entity to its dto model.
     *
     * @param filterFieldValue - filter field value entity
     * @return filter field value dto model
     */
    FilterFieldValueDto map(FilterFieldValue filterFieldValue);

    /**
     * Maps filter field values entities to its dto models.
     *
     * @param filterFieldValues - filter field values entities
     * @return filter field values dto models list
     */
    List<FilterFieldValueDto> map(List<FilterFieldValue> filterFieldValues);
}
