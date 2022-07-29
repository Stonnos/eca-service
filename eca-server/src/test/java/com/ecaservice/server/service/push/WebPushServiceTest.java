package com.ecaservice.server.service.push;

import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.ExperimentMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_STATUS_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_STATUS_MESSAGE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link WebPushService} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@Import({DateTimeConverter.class, ExperimentMapperImpl.class, InstancesInfoMapperImpl.class})
class WebPushServiceTest {

    private static final String MESSAGE_TEXT = "Message text";
    private static final long ID = 1L;

    @Mock
    private WebPushClient webPushClient;
    @Mock
    private MessageTemplateProcessor messageTemplateProcessor;

    private WebPushService webPushService;

    @Captor
    private ArgumentCaptor<PushRequestDto> pushRequestDtoArgumentCaptor;

    @BeforeEach
    void init() {
        webPushService = new WebPushService(messageTemplateProcessor, webPushClient);
    }

    @Test
    void testSendPush() {
        var experiment = createExperiment(UUID.randomUUID().toString());
        experiment.setId(ID);
        when(messageTemplateProcessor.process(anyString(), anyMap())).thenReturn(MESSAGE_TEXT);
        webPushService.sendWebPush(experiment);
        verify(webPushClient, atLeastOnce()).sendPush(pushRequestDtoArgumentCaptor.capture());
        var pushRequestDto = pushRequestDtoArgumentCaptor.getValue();
        assertThat(pushRequestDto).isNotNull();
        assertThat(pushRequestDto.getRequestId()).isNotNull();
        assertThat(pushRequestDto.getMessageType()).isEqualTo(EXPERIMENT_STATUS_MESSAGE_TYPE);
        assertThat(pushRequestDto.getAdditionalProperties())
                .containsEntry(EXPERIMENT_ID_PROPERTY, String.valueOf(experiment.getId()))
                .containsEntry(EXPERIMENT_REQUEST_ID_PROPERTY, experiment.getRequestId())
                .containsEntry(EXPERIMENT_REQUEST_STATUS_PROPERTY, experiment.getRequestStatus().name());
    }
}
