package com.ecaservice.core.form.template.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.form.template.entity.FormTemplateEntity;
import com.ecaservice.core.form.template.entity.FormTemplateGroupEntity;
import com.ecaservice.core.form.template.mapping.FormTemplateMapper;
import com.ecaservice.core.form.template.repository.FormTemplateGroupRepository;
import com.ecaservice.core.form.template.repository.FormTemplateRepository;
import com.ecaservice.web.dto.model.FormTemplateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ecaservice.core.form.template.config.CacheNames.FORM_TEMPLATES_GROUP_CACHE_NAME;
import static com.ecaservice.core.form.template.config.CacheNames.FORM_TEMPLATE_CACHE_NAME;

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
    private final FormTemplateRepository formTemplateRepository;

    /**
     * Gets form template by name.
     *
     * @param templateName - template name
     * @return form template dto
     */
    @Cacheable(FORM_TEMPLATE_CACHE_NAME)
    public FormTemplateDto getTemplate(String templateName) {
        log.debug("Gets form template [{}]", templateName);
        return formTemplateRepository.findByTemplateName(templateName)
                .map(formTemplateMapper::map)
                .orElseThrow(() -> new EntityNotFoundException(FormTemplateEntity.class, templateName));
    }

    /**
     * Gets form templates for specified group.
     *
     * @param groupName - group name
     * @return form templates
     */
    @Cacheable(FORM_TEMPLATES_GROUP_CACHE_NAME)
    public List<FormTemplateDto> getTemplates(String groupName) {
        log.debug("Gets form templates for group [{}]", groupName);
        var templates = formTemplateGroupRepository.findByGroupName(groupName)
                .map(FormTemplateGroupEntity::getTemplates)
                .orElseThrow(() -> new EntityNotFoundException(FormTemplateGroupEntity.class, groupName));
        log.debug("[{}] form templates has been fetched", templates.size());
        return formTemplateMapper.mapTemplates(templates);
    }
}