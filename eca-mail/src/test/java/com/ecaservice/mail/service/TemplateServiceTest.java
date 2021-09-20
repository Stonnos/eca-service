package com.ecaservice.mail.service;

import com.ecaservice.mail.AbstractJpaTest;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.repository.TemplateRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;

import static com.ecaservice.mail.TestHelperUtils.createTemplateEntity;
import static com.ecaservice.mail.model.BaseEntity_.CREATED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TemplateService} class.
 *
 * @author Roman Batygin
 */
@Import({TemplateService.class, MailConfig.class})
class TemplateServiceTest extends AbstractJpaTest {

    private static final String TEMPLATE_CODE_1 = "templateCode1";
    private static final String TEMPLATE_CODE_2 = "templateCode2";
    private static final int PAGE = 0;
    private static final int SIZE = 10;

    @Inject
    private TemplateRepository templateRepository;

    @Inject
    private TemplateService templateService;

    @Override
    public void deleteAll() {
        templateRepository.deleteAll();
    }

    @Test
    void testSearchByCode() {
        var first = createTemplateEntity(TEMPLATE_CODE_1);
        var second = createTemplateEntity(TEMPLATE_CODE_2);
        templateRepository.saveAll(Arrays.asList(first, second));
        var pageRequestDto =
                new PageRequestDto(PAGE, SIZE, CREATED, true, TEMPLATE_CODE_1, Collections.emptyList());
        Page<TemplateEntity> templatesPage = templateService.getNextPage(pageRequestDto);
        assertThat(templatesPage).isNotNull();
        assertThat(templatesPage.getContent()).hasSize(1);
        assertThat(templatesPage.getContent().iterator().next().getId()).isEqualTo(first.getId());
    }
}
