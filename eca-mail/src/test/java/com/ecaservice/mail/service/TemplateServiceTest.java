package com.ecaservice.mail.service;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.mail.AbstractJpaTest;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.repository.TemplateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static com.ecaservice.mail.TestHelperUtils.createTemplateEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for checking {@link TemplateService} functionality.
 *
 * @author Roman Batygin
 */
@Import(TemplateService.class)
class TemplateServiceTest extends AbstractJpaTest {

    private static final String TEST_TEMPLATE = "testTemplate";

    @Inject
    private TemplateService templateService;
    @Inject
    private TemplateRepository templateRepository;

    @Override
    public void deleteAll() {
        templateRepository.deleteAll();
    }

    @Test
    void testGetNotExistingTemplate() {
        assertThrows(EntityNotFoundException.class, () -> templateService.getTemplate(TEST_TEMPLATE));
    }

    @Test
    void testGetEmailTemplate() {
        TemplateEntity expected = createTemplateEntity();
        templateRepository.save(expected);
        TemplateEntity actual = templateService.getTemplate(expected.getCode());
        assertThat(actual).isNotNull();
        assertThat(actual.getCode()).isEqualTo(expected.getCode());
    }
}
