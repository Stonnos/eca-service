package com.notification.controller;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification.service.EmailService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.UUID;

import static com.ecaservice.notification.util.FieldConstraints.SUBJECT_MAX_SIZE;
import static com.notification.TestHelperUtils.createEmailRequest;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link EmailController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = EmailController.class)
public class EmailControllerTest {

    private static final String BASE_URL = "/emails";
    private static final String EMAIL_REQUEST_URL = BASE_URL + "/email-request";

    private static final String INVALID_EMAIL = "abcd.ru";

    @MockBean
    private EmailService emailService;

    @Inject
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSaveEmailRequestWithEmptyReceiverEmail() throws Exception {
        EmailRequest emailRequest = createEmailRequest();
        emailRequest.setReceiver(StringUtils.EMPTY);
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSaveEmailRequestWithInvalidReceiverEmail() throws Exception {
        EmailRequest emailRequest = createEmailRequest();
        emailRequest.setReceiver(INVALID_EMAIL);
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSaveEmailRequestWithEmptySubject() throws Exception {
        EmailRequest emailRequest = createEmailRequest();
        emailRequest.setSubject(StringUtils.EMPTY);
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSaveEmailRequestWithEmptyMessage() throws Exception {
        EmailRequest emailRequest = createEmailRequest();
        emailRequest.setMessage(StringUtils.EMPTY);
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSaveEmailRequestWithLargeSubject() throws Exception {
        EmailRequest emailRequest = createEmailRequest();
        emailRequest.setSubject(StringUtils.repeat('Q', SUBJECT_MAX_SIZE + 1));
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSaveEmailRequestOk() throws Exception {
        EmailRequest emailRequest = createEmailRequest();
        EmailResponse emailResponse = EmailResponse.builder().requestId(UUID.randomUUID().toString()).build();
        when(emailService.saveEmail(emailRequest)).thenReturn(emailResponse);
        mockMvc.perform(post(EMAIL_REQUEST_URL)
                .content(objectMapper.writeValueAsString(emailRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(emailResponse)));
    }
}