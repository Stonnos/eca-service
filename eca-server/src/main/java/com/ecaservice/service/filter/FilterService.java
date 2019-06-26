package com.ecaservice.service.filter;

import com.ecaservice.config.EcaServiceParam;
import com.ecaservice.mapping.filters.FilterFieldMapper;
import com.ecaservice.model.entity.FilterTemplate;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.GlobalFilterField;
import com.ecaservice.model.entity.GlobalFilterTemplate;
import com.ecaservice.repository.FilterTemplateRepository;
import com.ecaservice.repository.GlobalFilterTemplateRepository;
import com.ecaservice.web.dto.model.FilterFieldDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Global filter service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class FilterService {

    private final FilterFieldMapper filterFieldMapper;
    private final GlobalFilterTemplateRepository globalFilterTemplateRepository;
    private final FilterTemplateRepository filterTemplateRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param filterFieldMapper              - filter field mapper bean
     * @param globalFilterTemplateRepository - global filter template repository bean
     * @param filterTemplateRepository       - filter template repository bean
     */
    @Inject
    public FilterService(FilterFieldMapper filterFieldMapper,
                         GlobalFilterTemplateRepository globalFilterTemplateRepository,
                         FilterTemplateRepository filterTemplateRepository) {
        this.filterFieldMapper = filterFieldMapper;
        this.globalFilterTemplateRepository = globalFilterTemplateRepository;
        this.filterTemplateRepository = filterTemplateRepository;
    }

    /**
     * Finds global filter fields by template type.
     *
     * @param filterTemplateType - filter template type
     * @return global filter fields list
     */
    @Cacheable(EcaServiceParam.GLOBAL_FILTERS_CACHE_NAME)
    public List<String> getGlobalFilterFields(FilterTemplateType filterTemplateType) {
        GlobalFilterTemplate globalFilterTemplate =
                globalFilterTemplateRepository.findFirstByTemplateType(filterTemplateType);
        return Optional.ofNullable(globalFilterTemplate).map(GlobalFilterTemplate::getFields).map(
                globalFilterFields -> globalFilterFields.stream().map(GlobalFilterField::getFieldName).collect(
                        Collectors.toList())).orElse(Collections.emptyList());
    }

    /**
     * Gets filter template fields by template type.
     *
     * @param templateType - filter template type
     * @return filter field dto list
     */
    @Cacheable(EcaServiceParam.FILTER_TEMPLATES_CACHE_NAME)
    public List<FilterFieldDto> getFilterFields(FilterTemplateType templateType) {
        log.info("Fetch filter fields for template type [{}]", templateType);
        FilterTemplate filterTemplate = filterTemplateRepository.findFirstByTemplateType(templateType);
        if (filterTemplate == null) {
            throw new IllegalArgumentException(
                    String.format("Can't find filter template with type [%s]", templateType));
        }
        return filterFieldMapper.map(filterTemplate.getFields());
    }
}
