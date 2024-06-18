package com.ecaservice.core.form.template.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.form.template.entity.FormTemplateGroupEntity;
import com.ecaservice.core.form.template.mapping.FormTemplateMapper;
import com.ecaservice.core.form.template.repository.FormTemplateGroupRepository;
import com.ecaservice.web.dto.model.FormTemplateGroupDto;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

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

    private LoadingCache<String, FormTemplateGroupEntity> templatesCache;

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
     * Gets form templates dto for specified group.
     *
     * @param groupName - group name
     * @return form templates
     */
    public FormTemplateGroupDto getFormGroupDto(String groupName) {
        var formTemplateGroupEntity = getFormGroup(groupName);
        return formTemplateMapper.map(formTemplateGroupEntity);
    }

    /**
     * Gets form templates entity for specified group.
     *
     * @param groupName - group name
     * @return form templates
     */
    public FormTemplateGroupEntity getFormGroup(String groupName) {
        log.debug("Gets form templates for group [{}]", groupName);
        var formTemplateGroupEntity = templatesCache.getUnchecked(groupName);
        log.debug("[{}] form group [{}] templates has been fetched", groupName,
                formTemplateGroupEntity.getTemplates().size());
        return formTemplateGroupEntity;
    }

    private CacheLoader<String, FormTemplateGroupEntity> createCacheLoader() {
        return new CacheLoader<>() {
            @Override
            public FormTemplateGroupEntity load(String groupName) {
                log.info("Gets form templates group [{}] from db", groupName);
                var formTemplateGroupEntity = formTemplateGroupRepository.findByGroupName(groupName)
                        .orElseThrow(() -> new EntityNotFoundException(FormTemplateGroupEntity.class, groupName));
                log.info("[{}] group [{}] form templates has been fetched from db", groupName,
                        formTemplateGroupEntity.getTemplates().size());
                return formTemplateGroupEntity;
            }
        };
    }
}
