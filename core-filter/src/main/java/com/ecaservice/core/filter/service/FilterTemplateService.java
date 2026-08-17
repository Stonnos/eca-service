package com.ecaservice.core.filter.service;

import com.ecaservice.core.filter.mapping.FilterTemplateMapper;
import com.ecaservice.core.filter.model.FilterTemplate;
import com.ecaservice.web.dto.model.FilterDictionaryDto;
import com.ecaservice.web.dto.model.FilterFieldDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Filter template service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilterTemplateService {

    private final FilterTemplateRepository filterTemplateRepository;
    private final FilterTemplateMapper filterTemplateMapper;


    /**
     * Finds global filter fields by template type.
     *
     * @param templateType - filter template type
     * @return global filter fields list
     */
    public List<String> getGlobalFilterFields(String templateType) {
        var filterTemplate = getFilterTemplate(templateType);
        return Optional.ofNullable(filterTemplate.getGlobalFilterFields()).orElse(Collections.emptyList());
    }

    /**
     * Gets filter template fields by template type.
     *
     * @param templateType - filter template type
     * @return filter field dto list
     */
    public List<FilterFieldDto> getFilterFields(String templateType) {
        log.debug("Fetch filter fields for template type [{}]", templateType);
        var filterTemplate = getFilterTemplate(templateType);
        var filterFields = Optional.ofNullable(filterTemplate.getFields())
                .map(filterTemplateMapper::mapFields)
                .orElse(Collections.emptyList());
        log.debug("Filter fields has been fetched for template type [{}]", templateType);
        return filterFields;
    }

    /**
     * Gets filter dictionary by name.
     *
     * @param name - filter dictionary name
     * @return filter dictionary dto
     */
    public FilterDictionaryDto getFilterDictionary(String name) {
        log.debug("Fetch filter dictionary with name [{}]", name);
        return filterTemplateRepository.getDictionaries().stream()
                .filter(dictionary -> dictionary.getName().equals(name))
                .findFirst()
                .map(filterTemplateMapper::map)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Filter dictionary [%s] not found", name)));
    }

    /**
     * Finds sort fields by template type.
     *
     * @param templateType - sort template type
     * @return sort fields list
     */
    public List<String> getSortFields(String templateType) {
        log.debug("Gets sort fields with template [{}]", templateType);
        var filterTemplate = getFilterTemplate(templateType);
        var sortFieldsList = Optional.ofNullable(filterTemplate.getSortFields()).orElse(Collections.emptyList());
        log.debug("{} sort fields has been fetched for template [{}]", sortFieldsList, templateType);
        return sortFieldsList;
    }

    private FilterTemplate getFilterTemplate(String templateType) {
        return filterTemplateRepository.getTemplates().stream()
                .filter(template -> template.getTemplateType().equals(templateType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Filter template [%s] not found", templateType)));
    }
}
