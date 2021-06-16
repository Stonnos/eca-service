package com.ecaservice.core.filter.mapping;

import com.ecaservice.core.filter.entity.FilterDictionaryValue;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Filter field value mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface FilterDictionaryValueMapper {

    /**
     * Maps filter field value entity to its dto model.
     *
     * @param filterDictionaryValue - filter field value entity
     * @return filter field value dto model
     */
    FilterDictionaryValueDto map(FilterDictionaryValue filterDictionaryValue);

    /**
     * Maps filter field values entities to its dto models.
     *
     * @param filterDictionaryValues - filter field values entities
     * @return filter field values dto models list
     */
    List<FilterDictionaryValueDto> map(List<FilterDictionaryValue> filterDictionaryValues);
}
