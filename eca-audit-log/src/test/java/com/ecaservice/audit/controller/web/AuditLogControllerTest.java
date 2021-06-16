package com.ecaservice.audit.controller.web;

import com.ecaservice.audit.entity.AuditLogEntity;
import com.ecaservice.audit.mapping.AuditLogMapper;
import com.ecaservice.audit.mapping.AuditLogMapperImpl;
import com.ecaservice.audit.service.AuditLogService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";

    private static final long TOTAL_ELEMENTS = 1L;
    private static final int PAGE_NUMBER = 0;

    @MockBean
    private AuditLogService auditLogService;
    @MockBean
    private FilterService filterService;

    @Inject
    private AuditLogMapper auditLogMapper;

    @Test
    void testGetAuditLogsPageUnauthorized() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .param(PAGE_PARAM, String.valueOf(PAGE_NUMBER))
                .param(SIZE_PARAM, String.valueOf(TOTAL_ELEMENTS)))
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
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .param(PAGE_PARAM, String.valueOf(PAGE_NUMBER))
                .param(SIZE_PARAM, String.valueOf(TOTAL_ELEMENTS)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }
}
