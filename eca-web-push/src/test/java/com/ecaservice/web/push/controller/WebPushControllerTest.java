package com.ecaservice.web.push.controller;

import com.ecaservice.common.web.annotation.EnableGlobalExceptionHandler;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.push.config.ws.QueueConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static com.ecaservice.web.push.TestHelperUtils.createExperimentDto;
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
    private static final String EXPERIMENT_PUSH_URL = BASE_URL + "/experiment";

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
    void testPushExperiment() throws Exception {
        var experimentDto = createExperimentDto();
        mockMvc.perform(post(EXPERIMENT_PUSH_URL)
                .content(objectMapper.writeValueAsString(experimentDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(messagingTemplate, atLeastOnce()).convertAndSend(destinationCaptor.capture(), any(ExperimentDto.class));
        assertThat(destinationCaptor.getValue()).isNotNull();
        assertThat(destinationCaptor.getValue()).isEqualTo(queueConfig.getExperimentQueue());
    }
}
