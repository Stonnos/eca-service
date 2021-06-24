package com.ecaservice.audit.controller.audit;

import com.ecaservice.audit.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static com.ecaservice.audit.TestHelperUtils.createAuditEventRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link AuditEventController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = AuditEventController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditEventControllerTest {

    private static final String BASE_URL = "/api/audit/event";
    private static final String SAVE_URL = BASE_URL + "/save";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private AuditLogService auditLogService;

    @Inject
    private MockMvc mockMvc;

    @Test
    void testSaveEvent() throws Exception {
        var auditEventRequest = createAuditEventRequest();
        mockMvc.perform(post(SAVE_URL)
                .content(objectMapper.writeValueAsString(auditEventRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
