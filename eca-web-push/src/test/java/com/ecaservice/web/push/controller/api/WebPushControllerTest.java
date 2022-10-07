package com.ecaservice.web.push.controller.api;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import com.ecaservice.web.push.config.ws.QueueConfig;
import com.ecaservice.web.push.controller.api.WebPushController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import static com.ecaservice.web.push.TestHelperUtils.createPushRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link WebPushController} functionality.
 *
 * @author Roman Batygin
 */
@EnableGlobalExceptionHandler
@WebMvcTest(controllers = WebPushController.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import(QueueConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class WebPushControllerTest {

    private static final String BASE_URL = "/push";
    private static final String SEND_PUSH_URL = BASE_URL + "/send";;
    private static final int INVALID_SIZE = 256;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Inject
    private MockMvc mockMvc;
    @Inject
    private QueueConfig queueConfig;

    @Captor
    private ArgumentCaptor<String> destinationCaptor;

    @Test
    void testSendPush() throws Exception {
        var pushRequestDto = createPushRequestDto();
        mockMvc.perform(post(SEND_PUSH_URL)
                .content(objectMapper.writeValueAsString(pushRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(messagingTemplate, atLeastOnce()).convertAndSend(destinationCaptor.capture(), any(PushRequestDto.class));
        assertThat(destinationCaptor.getValue()).isNotNull();
        assertThat(destinationCaptor.getValue()).isEqualTo(queueConfig.getPushQueue());
    }

    @Test
    void testSendPushWithEmptyRequestId() throws Exception {
        var pushRequestDto = createPushRequestDto();
        pushRequestDto.setRequestId(null);
        internalTestBadRequest(pushRequestDto);
    }

    @Test
    void testSendPushWithEmptyMessageType() throws Exception {
        var pushRequestDto = createPushRequestDto();
        pushRequestDto.setMessageType(null);
        internalTestBadRequest(pushRequestDto);
    }

    @Test
    void testSendPushWithInvalidMessageTypeSize() throws Exception {
        var pushRequestDto = createPushRequestDto();
        pushRequestDto.setMessageType(StringUtils.repeat('Q', INVALID_SIZE));
        internalTestBadRequest(pushRequestDto);
    }

    @Test
    void testSendPushWithInvalidMessageTextSize() throws Exception {
        var pushRequestDto = createPushRequestDto();
        pushRequestDto.setMessageType(StringUtils.repeat('Q', INVALID_SIZE));
        internalTestBadRequest(pushRequestDto);
    }

    private void internalTestBadRequest(PushRequestDto pushRequestDto) throws Exception {
        mockMvc.perform(post(SEND_PUSH_URL)
                .content(objectMapper.writeValueAsString(pushRequestDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
