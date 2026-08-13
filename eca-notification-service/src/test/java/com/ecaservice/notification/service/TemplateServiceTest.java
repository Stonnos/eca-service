package com.ecaservice.notification.service;

import com.ecaservice.core.filter.config.CoreFilterConfiguration;
import com.ecaservice.core.filter.mapping.FilterTemplateMapperImpl;
import com.ecaservice.notification.AbstractJpaTest;
import com.ecaservice.notification.config.MailProperties;
import com.ecaservice.notification.entity.TemplateEntity;
import com.ecaservice.notification.repository.TemplateRepository;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.SortFieldRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.Collections;

import static com.ecaservice.notification.TestHelperUtils.createTemplateEntity;
import static com.ecaservice.notification.entity.BaseEntity_.CREATED;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TemplateService} class.
 *
 * @author Roman Batygin
 */
@Import({TemplateService.class, MailProperties.class, CoreFilterConfiguration.class, FilterTemplateMapperImpl.class})
class TemplateServiceTest extends AbstractJpaTest {

    private static final String TEMPLATE_CODE_1 = "templateCode1";
    private static final String TEMPLATE_CODE_2 = "templateCode2";
    private static final int PAGE = 0;
    private static final int SIZE = 10;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
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
                new PageRequestDto(PAGE, SIZE, Collections.singletonList(new SortFieldRequestDto(CREATED, true)),
                        TEMPLATE_CODE_1, Collections.emptyList());
        Page<TemplateEntity> templatesPage = templateService.getNextPage(pageRequestDto);
        assertThat(templatesPage).isNotNull();
        assertThat(templatesPage.getContent()).hasSize(1);
        assertThat(templatesPage.getContent().iterator().next().getId()).isEqualTo(first.getId());
    }
}
