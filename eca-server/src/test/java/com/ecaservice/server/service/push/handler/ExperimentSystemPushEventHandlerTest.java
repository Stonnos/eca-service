package com.ecaservice.server.service.push.handler;

import com.ecaservice.server.event.model.push.ExperimentSystemPushEvent;
import com.ecaservice.server.service.experiment.ExperimentMessageTemplateProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_STATUS_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_STATUS_MESSAGE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ExperimentSystemPushEventHandler} class.
 *
 * @author Roman Batygin
 */
@ExtendWith(MockitoExtension.class)
class ExperimentSystemPushEventHandlerTest {

    private static final String MESSAGE_TEXT = "Message text";
    private static final long ID = 1L;

    @Mock
    private ExperimentMessageTemplateProcessor messageTemplateProcessor;

    @InjectMocks
    private ExperimentSystemPushEventHandler experimentSystemPushEventHandler;


    @Test
    void testHandleExperimentPush() {
        var experiment = createExperiment(UUID.randomUUID().toString());
        experiment.setId(ID);
        when(messageTemplateProcessor.process(experiment)).thenReturn(MESSAGE_TEXT);
        var event = new ExperimentSystemPushEvent(this, experiment);
        var pushRequest = experimentSystemPushEventHandler.handleEvent(event);
        assertThat(pushRequest).isNotNull();
        assertThat(pushRequest.getRequestId()).isNotNull();
        assertThat(pushRequest.getMessageType()).isEqualTo(EXPERIMENT_STATUS_MESSAGE_TYPE);
        assertThat(pushRequest.getAdditionalProperties())
                .containsEntry(EXPERIMENT_ID_PROPERTY, String.valueOf(experiment.getId()))
                .containsEntry(EXPERIMENT_REQUEST_ID_PROPERTY, experiment.getRequestId())
                .containsEntry(EXPERIMENT_REQUEST_STATUS_PROPERTY, experiment.getRequestStatus().name());
    }
}
