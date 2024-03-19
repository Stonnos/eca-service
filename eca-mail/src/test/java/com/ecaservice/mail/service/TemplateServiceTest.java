package com.ecaservice.mail.service;

import com.ecaservice.core.filter.service.FilterTemplateService;
import com.ecaservice.mail.AbstractJpaTest;
import com.ecaservice.mail.config.MailConfig;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.model.TemplateEntity_;
import com.ecaservice.mail.repository.TemplateRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.mail.TestHelperUtils.createTemplateEntity;
import static com.ecaservice.mail.dictionary.FilterDictionaries.EMAIL_TEMPLATES;
import static com.ecaservice.mail.model.BaseEntity_.CREATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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

    @MockBean
    private FilterTemplateService filterTemplateService;

    @Inject
    private TemplateRepository templateRepository;

    @Inject
    private TemplateService templateService;

    @Override
    public void init() {
        when(filterTemplateService.getGlobalFilterFields(EMAIL_TEMPLATES)).thenReturn(
                List.of(
                        TemplateEntity_.CODE,
                        TemplateEntity_.DESCRIPTION,
                        TemplateEntity_.SUBJECT
                ));
    }

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
                new PageRequestDto(PAGE, SIZE, Collections.singletonList(new SortFieldRequestDto(CREATED, true)),
                        TEMPLATE_CODE_1, Collections.emptyList());
        Page<TemplateEntity> templatesPage = templateService.getNextPage(pageRequestDto);
        assertThat(templatesPage).isNotNull();
        assertThat(templatesPage.getContent()).hasSize(1);
        assertThat(templatesPage.getContent().iterator().next().getId()).isEqualTo(first.getId());
    }
}
