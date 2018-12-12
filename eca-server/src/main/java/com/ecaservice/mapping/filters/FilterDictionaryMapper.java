package com.ecaservice.mapping.filters;

import com.ecaservice.model.entity.FilterDictionary;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import org.mapstruct.Mapper;

/**
 * Filter dictionary mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = FilterDictionaryValueMapper.class)
public interface FilterDictionaryMapper {

    /**
     * Maps filter dictionary entity to its dto model.
     *
     * @param filterDictionary - filter dictionary entity
     * @return filter dictionary dto
     */
    FilterDictionaryDto map(FilterDictionary filterDictionary);
}
