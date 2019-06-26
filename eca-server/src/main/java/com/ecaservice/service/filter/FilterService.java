package com.ecaservice.service.filter;

import com.ecaservice.config.EcaServiceParam;
import com.ecaservice.model.entity.FilterTemplateType;
import com.ecaservice.model.entity.GlobalFilterField;
import com.ecaservice.model.entity.GlobalFilterTemplate;
import com.ecaservice.repository.GlobalFilterTemplateRepository;
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
@Service
public class FilterService {

    private final GlobalFilterTemplateRepository globalFilterTemplateRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param globalFilterTemplateRepository - global filter template repository bean
     */
    @Inject
    public FilterService(GlobalFilterTemplateRepository globalFilterTemplateRepository) {
        this.globalFilterTemplateRepository = globalFilterTemplateRepository;
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
}
