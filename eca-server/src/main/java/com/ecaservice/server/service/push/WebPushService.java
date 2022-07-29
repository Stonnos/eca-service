package com.ecaservice.server.service.push;

import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatusVisitor;
import com.ecaservice.server.service.message.template.MessageTemplateProcessor;
import com.ecaservice.web.dto.model.push.PushRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.ERROR_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.FINISHED_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.IN_PROGRESS_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.NEW_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateCodes.TIMEOUT_EXPERIMENT;
import static com.ecaservice.server.service.message.template.dictionary.MessageTemplateVariables.EXPERIMENT;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_ID_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_REQUEST_STATUS_PROPERTY;
import static com.ecaservice.server.service.push.dictionary.PushProperties.EXPERIMENT_STATUS_MESSAGE_TYPE;

/**
 * Service for sending web pushes to client.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebPushService {

    private final MessageTemplateProcessor messageTemplateProcessor;
    private final WebPushClient webPushClient;

    /**
     * Send web push with experiment changes.
     *
     * @param experiment - experiment entity
     */
    public void sendWebPush(Experiment experiment) {
        log.info("Starting to sent web push for experiment [{}], request status [{}]", experiment.getRequestId(),
                experiment.getRequestStatus());
        try {
            var pushRequest = buildPushRequest(experiment);
            webPushClient.sendPush(pushRequest);
            log.info("Web push has been sent for experiment [{}], request status [{}]", experiment.getRequestId(),
                    experiment.getRequestStatus());
        } catch (Exception ex) {
            log.error("There was an error while sending web push for experiment [{}], status [{}]: {}",
                    experiment.getRequestId(), experiment.getRequestStatus(), ex.getMessage());
        }
    }

    private PushRequestDto buildPushRequest(Experiment experiment) {
        var pushRequest = new PushRequestDto();
        pushRequest.setRequestId(UUID.randomUUID().toString());
        pushRequest.setMessageType(EXPERIMENT_STATUS_MESSAGE_TYPE);
        String messageText = processMessageText(experiment);
        pushRequest.setMessageText(messageText);
        pushRequest.setAdditionalProperties(Map.of(
                EXPERIMENT_REQUEST_ID_PROPERTY, experiment.getRequestId(),
                EXPERIMENT_REQUEST_STATUS_PROPERTY, experiment.getRequestStatus().name()
        ));
        return pushRequest;
    }

    private String processMessageText(Experiment experiment) {
        return experiment.getRequestStatus().handle(new RequestStatusVisitor<>() {
            @Override
            public String caseNew(Experiment exp) {
                return messageTemplateProcessor.process(NEW_EXPERIMENT, Collections.singletonMap(EXPERIMENT, exp));
            }

            @Override
            public String caseFinished(Experiment exp) {
                return messageTemplateProcessor.process(FINISHED_EXPERIMENT, Collections.singletonMap(EXPERIMENT, exp));
            }

            @Override
            public String caseTimeout(Experiment exp) {
                return messageTemplateProcessor.process(TIMEOUT_EXPERIMENT, Collections.singletonMap(EXPERIMENT, exp));
            }

            @Override
            public String caseError(Experiment exp) {
                return messageTemplateProcessor.process(ERROR_EXPERIMENT, Collections.singletonMap(EXPERIMENT, exp));
            }

            @Override
            public String caseInProgress(Experiment exp) {
                return messageTemplateProcessor.process(IN_PROGRESS_EXPERIMENT,
                        Collections.singletonMap(EXPERIMENT, exp));
            }
        }, experiment);
    }
}
