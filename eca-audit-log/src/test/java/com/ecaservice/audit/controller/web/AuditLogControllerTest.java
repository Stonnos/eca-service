package com.ecaservice.audit.controller.web;

import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.mapping.AuditLogMapper;
import com.ecaservice.audit.mapping.AuditLogMapperImpl;
import com.ecaservice.audit.report.AuditLogsBaseReportDataFetcher;
import com.ecaservice.audit.service.AuditLogService;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.core.filter.service.FilterService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.ecaservice.web.dto.model.AuditLogDto;
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

import static com.ecaservice.audit.TestHelperUtils.createAuditLog;
import static com.ecaservice.audit.TestHelperUtils.createFilterFieldDto;
import static com.ecaservice.audit.TestHelperUtils.createPageRequestDto;
import static com.ecaservice.audit.dictionary.FilterDictionaries.AUDIT_LOG_TEMPLATE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link AuditLogController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = AuditLogController.class)
@Import(AuditLogMapperImpl.class)
class AuditLogControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/audit-log";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String AUDIT_LOG_TEMPLATE_URL = BASE_URL + "/filter-templates/fields";
    private static final String DOWNLOAD_REPORT_URL = BASE_URL + "/report/download";

    private static final long TOTAL_ELEMENTS = 1L;
    private static final int PAGE_NUMBER = 0;

    @MockBean
    private AuditLogService auditLogService;
    @MockBean
    private FilterService filterService;
    @MockBean
    private AuditLogsBaseReportDataFetcher auditLogsBaseReportDataFetcher;

    @Inject
    private AuditLogMapper auditLogMapper;

    @Test
    void testGetAuditLogsPageUnauthorized() throws Exception {
        mockMvc.perform(post(LIST_URL)
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAuditLogsPage() throws Exception {
        Page<AuditLogEntity> page = Mockito.mock(Page.class);
        when(page.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        List<AuditLogEntity> auditLogs = Collections.singletonList(createAuditLog());
        PageDto<AuditLogDto> expected = PageDto.of(auditLogMapper.map(auditLogs), PAGE_NUMBER, TOTAL_ELEMENTS);
        when(page.getContent()).thenReturn(auditLogs);
        when(auditLogService.getNextPage(any(PageRequestDto.class))).thenReturn(page);
        mockMvc.perform(post(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testGetAuditLogFilterTemplateUnauthorized() throws Exception {
        mockMvc.perform(get(AUDIT_LOG_TEMPLATE_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAuditLogFilterTemplateBadRequest() throws Exception {
        when(filterService.getFilterFields(AUDIT_LOG_TEMPLATE)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(AUDIT_LOG_TEMPLATE_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAuditLogFilterTemplateOk() throws Exception {
        var filterFieldDtoList = Collections.singletonList(createFilterFieldDto());
        when(filterService.getFilterFields(AUDIT_LOG_TEMPLATE)).thenReturn(filterFieldDtoList);
        mockMvc.perform(get(AUDIT_LOG_TEMPLATE_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(filterFieldDtoList)));
    }

    @Test
    void testDownloadReportUnauthorized() throws Exception {
        mockMvc.perform(post(DOWNLOAD_REPORT_URL)
                .content(objectMapper.writeValueAsString(createPageRequestDto()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
