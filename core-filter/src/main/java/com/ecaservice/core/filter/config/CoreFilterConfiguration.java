package com.ecaservice.core.filter.config;

import com.ecaservice.common.web.resource.JsonResourceLoader;
import com.ecaservice.core.filter.cache.PageRequestKeyGenerator;
import com.ecaservice.core.filter.model.FilterDictionary;
import com.ecaservice.core.filter.model.FilterTemplate;
import com.ecaservice.core.filter.service.FilterTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Core filter configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@ComponentScan({"com.ecaservice.core.filter"})
public class CoreFilterConfiguration {

    /**
     * Page request cache key generator
     */
    public static final String PAGE_REQUEST_KEY_GENERATOR = "pageRequestKeyGenerator";

    private static final String FILTER_TEMPLATES_DICTIONARIES_JSON =
            "classpath*:filter-templates/dictionaries/**/*.json";
    private static final String FILTER_TEMPLATES_JSON = "classpath*:filter-templates/templates/**/*.json";

    private final JsonResourceLoader jsonResourceLoader = new JsonResourceLoader();


    /**
     * Creates page request cache key generator.
     *
     * @return page request cache key generator
     */
    @Bean(PAGE_REQUEST_KEY_GENERATOR)
    @ConditionalOnMissingBean
    public PageRequestKeyGenerator pageRequestKeyGenerator() {
        return new PageRequestKeyGenerator();
    }

    /**
     * Creates filter template repository bean.
     *
     * @return filter template repository bea
     */
    @Bean
    public FilterTemplateRepository filterTemplateRepository() {
        var dictionaries = jsonResourceLoader.loadAll(FILTER_TEMPLATES_DICTIONARIES_JSON, FilterDictionary.class);
        var templates = jsonResourceLoader.loadAll(FILTER_TEMPLATES_JSON, FilterTemplate.class);
        setDictionaries(dictionaries, templates);
        FilterTemplateRepository filterTemplateRepository = new FilterTemplateRepository(dictionaries, templates);
        log.info("Filter templates repository has been initialized");
        return filterTemplateRepository;
    }

    private void setDictionaries(List<FilterDictionary> dictionaries,
                                 List<FilterTemplate> templates) {
        templates.stream()
                .filter(template -> !CollectionUtils.isEmpty(template.getFields()))
                .flatMap(template -> template.getFields().stream())
                .filter(field -> field.getDictionary() != null)
                .forEach(field -> {
                    String dictionaryName = field.getDictionary().getName();
                    FilterDictionary dictionaryConfig = dictionaries.stream()
                            .filter(dictionary -> dictionary.getName().equals(dictionaryName))
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException(
                                    String.format("Can't set dictionary [%s]", dictionaryName))
                            );
                    field.setDictionary(dictionaryConfig);
                });
    }
}
