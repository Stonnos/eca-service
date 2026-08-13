package com.ecaservice.core.filter.mapping;

import com.ecaservice.core.filter.model.FilterDictionary;
import com.ecaservice.core.filter.model.FilterDictionaryValue;
import com.ecaservice.core.filter.model.FilterField;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterDictionaryValueDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Filter field mapper.
 *
 * @author Roman Batygin
 */
@Mapper
public interface FilterTemplateMapper {

    /**
     * Maps filter field to its dto model.
     *
     * @param filterField - filter field
     * @return filter field dto model
     */
    FilterFieldDto map(FilterField filterField);

    /**
     * Maps filter field to its dto models.
     *
     * @param filterFields - filter field
     * @return filter fields dto models list
     */
    List<FilterFieldDto> mapFields(List<FilterField> filterFields);

    /**
     * Maps filter dictionary to its dto model.
     *
     * @param filterDictionary - filter dictionary
     * @return filter dictionary dto
     */
    FilterDictionaryDto map(FilterDictionary filterDictionary);

    /**
     * Maps filter field value to its dto model.
     *
     * @param filterDictionaryValue - filter field value
     * @return filter field value dto model
     */
    FilterDictionaryValueDto map(FilterDictionaryValue filterDictionaryValue);

    /**
     * Maps filter field values to its dto models.
     *
     * @param filterDictionaryValues - filter field values
     * @return filter field values dto models list
     */
    List<FilterDictionaryValueDto> mapValues(List<FilterDictionaryValue> filterDictionaryValues);
}
