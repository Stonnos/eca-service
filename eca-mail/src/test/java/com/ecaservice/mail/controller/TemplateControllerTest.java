package com.ecaservice.mail.controller;

import com.ecaservice.mail.mapping.TemplateMapper;
import com.ecaservice.mail.mapping.TemplateMapperImpl;
import com.ecaservice.mail.model.TemplateEntity;
import com.ecaservice.mail.service.TemplateService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.EmailTemplateDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.ecaservice.mail.TestHelperUtils.createPageRequestDto;
import static com.ecaservice.mail.TestHelperUtils.createTemplateEntity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link TemplateController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = TemplateController.class)
@Import(TemplateMapperImpl.class)
class TemplateControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/templates";
    private static final String LIST_URL = BASE_URL + "/list";

    private static final int PAGE = 0;
    private static final int TOTAL_COUNT = 1;
    private static final long TOTAL_ELEMENTS = 1L;

    @MockBean
    private TemplateService templateService;

    @Inject
    private TemplateMapper templateMapper;

    @Test
    void testGetTemplatesPageUnauthorized() throws Exception {
        mockMvc.perform(post(LIST_URL)
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTemplatesPage() throws Exception {
        Page<TemplateEntity> templatesPage = Mockito.mock(Page.class);
        when(templatesPage.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        List<TemplateEntity> templatesList = Collections.singletonList(createTemplateEntity());
        PageDto<EmailTemplateDto> expected = PageDto.of(
                templateMapper.mapTemplates(templatesList), PAGE, TOTAL_COUNT);
        when(templatesPage.getContent()).thenReturn(templatesList);
        when(templateService.getNextPage(any(PageRequestDto.class))).thenReturn(templatesPage);
        mockMvc.perform(post(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }
}
