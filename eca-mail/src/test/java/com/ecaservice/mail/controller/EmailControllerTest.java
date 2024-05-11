package com.ecaservice.mail.controller;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.mail.TestHelperUtils;
import com.ecaservice.mail.mapping.EmailRequestMapper;
import com.ecaservice.mail.mapping.EmailRequestMapperImpl;
import com.ecaservice.mail.model.Email;
import com.ecaservice.mail.model.EmailStatus;
import com.ecaservice.mail.service.EmailService;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link EmailController} functionality.
 *
 * @author Roman Batygin
 */
@Disabled
@EnableGlobalExceptionHandler
@WebMvcTest(controllers = EmailController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(EmailRequestMapperImpl.class)
class EmailControllerTest {

    private static final String BASE_URL = "/emails";
    private static final String EMAIL_REQUEST_URL = BASE_URL + "/email-request";

    private static final String INVALID_EMAIL = "abcd.ru";

    @MockBean
    private EmailService emailService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailRequestMapper emailRequestMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSaveEmailRequestWithEmptyReceiverEmail() throws Exception {
        EmailRequest emailRequest = TestHelperUtils.createEmailRequest();
        emailRequest.setReceiver(StringUtils.EMPTY);
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveEmailRequestWithNullTemplateCode() throws Exception {
        EmailRequest emailRequest = TestHelperUtils.createEmailRequest();
        emailRequest.setTemplateCode(null);
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveEmailRequestWithInvalidReceiverEmail() throws Exception {
        EmailRequest emailRequest = TestHelperUtils.createEmailRequest();
        emailRequest.setReceiver(INVALID_EMAIL);
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSaveEmailRequestOk() throws Exception {
        EmailRequest emailRequest = TestHelperUtils.createEmailRequest();
        Email email = TestHelperUtils.createEmail(LocalDateTime.now(), EmailStatus.NEW);
        when(emailService.saveEmail(emailRequest)).thenReturn(email);
        EmailResponse emailResponse = emailRequestMapper.map(email);
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(emailResponse)));
    }
}
