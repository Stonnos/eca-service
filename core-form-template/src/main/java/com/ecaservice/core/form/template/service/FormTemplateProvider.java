package com.ecaservice.core.form.template.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.form.template.entity.FormTemplateGroupEntity;
import com.ecaservice.core.form.template.mapping.FormTemplateMapper;
import com.ecaservice.core.form.template.repository.FormTemplateGroupRepository;
import com.ecaservice.web.dto.model.FormTemplateDto;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Form templates provider service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FormTemplateProvider {

    private final FormTemplateMapper formTemplateMapper;
    private final FormTemplateGroupRepository formTemplateGroupRepository;

    private LoadingCache<String, List<FormTemplateDto>> templatesCache;

    /**
     * Initialize cache.
     */
    @PostConstruct
    public void initializeCache() {
        log.info("Starting to initialize form templates cache");
        this.templatesCache = CacheBuilder.newBuilder()
                .build(createCacheLoader());
        log.info("Form templates cache has been initialized");
    }

    /**
     * Gets form templates for specified group.
     *
     * @param groupName - group name
     * @return form templates
     */
    public List<FormTemplateDto> getTemplates(String groupName) {
        log.debug("Gets form templates for group [{}]", groupName);
        var templates = templatesCache.getUnchecked(groupName);
        log.debug("[{}] form templates has been fetched", templates.size());
        return templates;
    }

    private CacheLoader<String, List<FormTemplateDto>> createCacheLoader() {
        return new CacheLoader<>() {
            @Override
            public List<FormTemplateDto> load(String groupName) {
                log.info("Gets form templates group [{}] from db", groupName);
                var templates = formTemplateGroupRepository.findByGroupName(groupName)
                        .map(FormTemplateGroupEntity::getTemplates)
                        .orElseThrow(() -> new EntityNotFoundException(FormTemplateGroupEntity.class, groupName));
                log.info("[{}] form templates has been fetched from db", templates.size());
                return formTemplateMapper.mapTemplates(templates);
            }
        };
    }
}
