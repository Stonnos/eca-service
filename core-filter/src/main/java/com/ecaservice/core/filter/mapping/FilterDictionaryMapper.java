package com.ecaservice.core.filter.mapping;

import com.ecaservice.core.filter.entity.FilterDictionary;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

/**
 * Filter dictionary mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = FilterDictionaryValueMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FilterDictionaryMapper {

    /**
     * Maps filter dictionary entity to its dto model.
     *
     * @param filterDictionary - filter dictionary entity
     * @return filter dictionary dto
     */
    FilterDictionaryDto map(FilterDictionary filterDictionary);
}
