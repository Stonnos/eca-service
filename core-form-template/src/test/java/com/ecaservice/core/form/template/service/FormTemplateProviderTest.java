package com.ecaservice.core.form.template.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.form.template.AbstractJpaTest;
import com.ecaservice.core.form.template.entity.FormTemplateGroupEntity;
import com.ecaservice.core.form.template.mapping.FormTemplateMapperImpl;
import com.ecaservice.core.form.template.repository.FormTemplateGroupRepository;
import com.ecaservice.core.form.template.repository.FormTemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.core.form.template.TestHelperUtils.createFormTemplateEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link FormTemplateProvider} class.
 *
 * @author Roman Batygin
 */
@Import({FormTemplateProvider.class, FormTemplateMapperImpl.class})
class FormTemplateProviderTest extends AbstractJpaTest {

    private static final String GROUP_NAME = "groupName";
    private static final String INVALID_CODE = "invalidCode";

    @Inject
    private FormTemplateGroupRepository formTemplateGroupRepository;
    @Inject
    private FormTemplateRepository formTemplateRepository;

    @Inject
    private FormTemplateProvider formTemplateProvider;

    private FormTemplateGroupEntity formTemplateGroupEntity;
    
    @Override
    public void init() {
        formTemplateGroupEntity = createAndSaveFormTemplatesGroup();
    }
    
    @Override
    public void deleteAll() {
        formTemplateRepository.deleteAll();
        formTemplateGroupRepository.deleteAll();
    }

    @Test
    void testGetTemplate() {
        var template = formTemplateGroupEntity.getTemplates().iterator().next();
        var templateDto = formTemplateProvider.getTemplate(template.getTemplateName());
        assertThat(templateDto).isNotNull();
        assertThat(templateDto.getTemplateName()).isEqualTo(templateDto.getTemplateName());
        assertThat(templateDto.getFields()).hasSameSizeAs(template.getFields());
    }

    @Test
    void testGetTemplateShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> formTemplateProvider.getTemplate(INVALID_CODE));
    }

    @Test
    void testGetTemplates() {
        var templates = formTemplateProvider.getTemplates(formTemplateGroupEntity.getGroupName());
        assertThat(templates).hasSize(1);
    }

    @Test
    void testGetTemplatesShouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> formTemplateProvider.getTemplates(INVALID_CODE));
    }
    
    private FormTemplateGroupEntity createAndSaveFormTemplatesGroup() {
        var template = createFormTemplateEntity();
        var group = new FormTemplateGroupEntity();
        group.setGroupName(GROUP_NAME);
        group.setTemplates(Collections.singletonList(template));
        return formTemplateGroupRepository.save(group);
    }
}
