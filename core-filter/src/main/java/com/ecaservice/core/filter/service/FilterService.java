package com.ecaservice.core.filter.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.config.CacheNames;
import com.ecaservice.core.filter.entity.FilterDictionary;
import com.ecaservice.core.filter.entity.FilterTemplate;
import com.ecaservice.core.filter.entity.GlobalFilterField;
import com.ecaservice.core.filter.entity.GlobalFilterTemplate;
import com.ecaservice.core.filter.mapping.FilterDictionaryMapper;
import com.ecaservice.core.filter.mapping.FilterFieldMapper;
import com.ecaservice.core.filter.repository.FilterDictionaryRepository;
import com.ecaservice.core.filter.repository.FilterTemplateRepository;
import com.ecaservice.core.filter.repository.GlobalFilterTemplateRepository;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilterService {

    private final FilterFieldMapper filterFieldMapper;
    private final FilterDictionaryMapper filterDictionaryMapper;
    private final GlobalFilterTemplateRepository globalFilterTemplateRepository;
    private final FilterTemplateRepository filterTemplateRepository;
    private final FilterDictionaryRepository filterDictionaryRepository;

    /**
     * Finds global filter fields by template type.
     *
     * @param filterTemplateType - filter template type
     * @return global filter fields list
     */
    @Cacheable(CacheNames.GLOBAL_FILTERS_CACHE_NAME)
    public List<String> getGlobalFilterFields(String filterTemplateType) {
        return globalFilterTemplateRepository.findFirstByTemplateType(filterTemplateType).map(
                GlobalFilterTemplate::getFields).map(
                globalFilterFields -> globalFilterFields.stream().map(GlobalFilterField::getFieldName).collect(
                        Collectors.toList())).orElseThrow(
                () -> new EntityNotFoundException(GlobalFilterTemplate.class, filterTemplateType));
    }

    /**
     * Gets filter template fields by template type.
     *
     * @param templateType - filter template type
     * @return filter field dto list
     */
    @Cacheable(CacheNames.FILTER_TEMPLATES_CACHE_NAME)
    public List<FilterFieldDto> getFilterFields(String templateType) {
        log.info("Fetch filter fields for template type [{}]", templateType);
        return filterTemplateRepository.findFirstByTemplateType(templateType).map(FilterTemplate::getFields).map(
                filterFieldMapper::map).orElseThrow(
                () -> new EntityNotFoundException(FilterTemplate.class, templateType));
    }

    /**
     * Gets filter dictionary by name.
     *
     * @param name - filter dictionary name
     * @return filter dictionary dto
     */
    @Cacheable(CacheNames.FILTER_DICTIONARIES_CACHE_NAME)
    public FilterDictionaryDto getFilterDictionary(String name) {
        log.info("Fetch filter dictionary with name [{}]", name);
        return filterDictionaryRepository.findByName(name).map(filterDictionaryMapper::map).orElseThrow(
                () -> new EntityNotFoundException(FilterDictionary.class, name));
    }
}
