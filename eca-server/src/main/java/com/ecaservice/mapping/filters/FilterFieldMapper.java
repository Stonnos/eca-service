package com.ecaservice.mapping.filters;

import com.ecaservice.model.entity.FilterField;
import com.ecaservice.web.dto.model.FilterFieldDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Filter field mapper.
 *
 * @author Roman Batygin
 */
@Mapper(uses = FilterDictionaryMapper.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FilterFieldMapper {

    /**
     * Maps filter field entity to its dto model.
     *
     * @param filterField - filter field entity
     * @return filter field dto model
     */
    FilterFieldDto map(FilterField filterField);

    /**
     * Maps filter field entities to its dto models.
     *
     * @param filterFields - filter field entities
     * @return filter fields dto models list
     */
    List<FilterFieldDto> map(List<FilterField> filterFields);
}
