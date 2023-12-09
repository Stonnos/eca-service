package com.ecaservice.server.service.classifiers;

import com.ecaservice.classifier.template.processor.service.ClassifiersTemplateProvider;
import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.server.config.ClassifiersProperties;
import com.ecaservice.server.dto.ClassifierGroupTemplatesType;
import com.ecaservice.web.dto.model.FormTemplateGroupDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.ecaservice.server.TestHelperUtils.loadClassifiersTemplates;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ClassifiersFormTemplateProvider} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import({ClassifiersFormTemplateProvider.class, ClassifiersProperties.class, ClassifiersTemplateProvider.class})
class ClassifiersFormTemplateProviderTest {

    private static final String CLASSIFIERS = "classifiers";

    @MockBean
    private FormTemplateProvider formTemplateProvider;

    @Inject
    private ClassifiersFormTemplateProvider classifiersFormTemplateProvider;

    @BeforeEach
    void init() {
        FormTemplateGroupDto formTemplateGroupDto = loadClassifiersTemplates();
        when(formTemplateProvider.getFormGroupDto(CLASSIFIERS)).thenReturn(formTemplateGroupDto);
    }

    @Test
    void testGetTemplates() {
        var result = classifiersFormTemplateProvider.getClassifiersTemplates(ClassifierGroupTemplatesType.INDIVIDUAL);
        assertThat(result).hasSize(1);
    }
}
