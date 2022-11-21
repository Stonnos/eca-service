package com.ecaservice.web.push.controller.api;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.web.push.config.ws.QueueConfig;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import com.ecaservice.web.push.service.handler.SystemPushNotificationRequestHandler;
import com.ecaservice.web.push.service.handler.UserNotificationPushRequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.Collections;

import static com.ecaservice.web.push.TestHelperUtils.createSystemPushRequest;
import static com.ecaservice.web.push.TestHelperUtils.createUserPushNotificationRequest;
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

    private static final String BASE_URL = "/api/push";
    private static final String SEND_PUSH_URL = BASE_URL + "/send";
    private static final int INVALID_SIZE = 256;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UserNotificationPushRequestHandler userNotificationPushRequestHandler;
    @MockBean
    private SystemPushNotificationRequestHandler systemPushNotificationRequestHandler;

    @Inject
    private MockMvc mockMvc;

    @Test
    void testSendSystemPushWithEmptyRequestId() throws Exception {
        var systemPushRequest = createSystemPushRequest();
        systemPushRequest.setRequestId(null);
        internalTestBadRequest(systemPushRequest);
    }

    @Test
    void testSendPushWithEmptyMessageType() throws Exception {
        var systemPushRequest = createSystemPushRequest();
        systemPushRequest.setMessageType(null);
        internalTestBadRequest(systemPushRequest);
    }

    @Test
    void testSendPushWithInvalidMessageTypeSize() throws Exception {
        var systemPushRequest = createSystemPushRequest();
        systemPushRequest.setMessageType(StringUtils.repeat('Q', INVALID_SIZE));
        internalTestBadRequest(systemPushRequest);
    }

    @Test
    void testSendPushWithInvalidMessageTextSize() throws Exception {
        var systemPushRequest = createSystemPushRequest();
        systemPushRequest.setMessageType(StringUtils.repeat('Q', INVALID_SIZE));
        internalTestBadRequest(systemPushRequest);
    }

    @Test
    void testSendPushWithEmptyReceivers() throws Exception {
        var request = createUserPushNotificationRequest();
        request.setReceivers(Collections.emptyList());
        internalTestBadRequest(request);
    }

    @Test
    void testSendPushWithEmptyInitiator() throws Exception {
        var request = createUserPushNotificationRequest();
        request.setInitiator(StringUtils.EMPTY);
        internalTestBadRequest(request);
    }

    private void internalTestBadRequest(AbstractPushRequest pushRequest) throws Exception {
        mockMvc.perform(post(SEND_PUSH_URL)
                .content(objectMapper.writeValueAsString(pushRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
