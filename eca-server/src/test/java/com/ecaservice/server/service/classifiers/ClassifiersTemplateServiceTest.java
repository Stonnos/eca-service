package com.ecaservice.server.service.classifiers;

import com.ecaservice.core.form.template.service.FormTemplateProvider;
import com.ecaservice.web.dto.model.FormTemplateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;
import java.util.List;

import static com.ecaservice.server.TestHelperUtils.loadClassifiersTemplates;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ClassifiersTemplateService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@Import(ClassifiersTemplateService.class)
class ClassifiersTemplateServiceTest {

    private static final String CLASSIFIERS = "classifiers";

    @MockBean
    private FormTemplateProvider formTemplateProvider;

    @Inject
    private ClassifiersTemplateService classifiersTemplateService;

    private List<FormTemplateDto> templates;

    @BeforeEach
    void init() {
        templates = loadClassifiersTemplates();
        when(formTemplateProvider.getTemplates(CLASSIFIERS)).thenReturn(templates);
    }

    @Test
    void testGetTemplates() {
        var result = classifiersTemplateService.getClassifiersTemplates();
        assertThat(result).hasSameSizeAs(templates);
    }
}
