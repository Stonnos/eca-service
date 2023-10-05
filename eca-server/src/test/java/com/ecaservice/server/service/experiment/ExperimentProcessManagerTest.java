package com.ecaservice.server.service.experiment;

import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.ChannelVisitor;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.model.experiment.ExperimentMessageRequestData;
import com.ecaservice.server.repository.ExperimentRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractEvaluationProcessManagerTest;
import com.ecaservice.server.service.experiment.mail.ExperimentEmailTemplateVariable;
import com.ecaservice.server.verifier.EvaluationRequestStatusVerifier;
import com.ecaservice.server.verifier.TestStepVerifier;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import com.ecaservice.web.push.dto.PushType;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static com.ecaservice.server.TestHelperUtils.createExperimentMessageRequest;
import static com.ecaservice.server.TestHelperUtils.createExperimentWebRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link ExperimentProcessManager} class.
 *
 * @author Roman Batygin
 */
@SpringBootTest
@Deployment(resources = {"bpmn/process-experiment.bpmn", "bpmn/finish-experiment.bpmn",
        "bpmn/create-experiment-request-process.bpmn", "bpmn/create-experiment-web-request-process.bpmn",
        "bpmn/create-experiment-message-request-process.bpmn"})
class ExperimentProcessManagerTest extends AbstractEvaluationProcessManagerTest<Experiment> {

    private static final String PUSH_MESSAGE_TYPE = "EXPERIMENT_STATUS";
    private static final String EVALUATION_ID = "id";
    private static final String EVALUATION_REQUEST_ID = "requestId";
    private static final String EVALUATION_REQUEST_STATUS = "requestStatus";
    private static final int PROCESS_EXPERIMENT_TIMEOUT_SECONDS = 60;
    private static final String NEW_EXPERIMENT_EMAIL_TEMPLATE_CODE = "NEW_EXPERIMENT";
    private static final String IN_PROGRESS_EXPERIMENT_EMAIL_TEMPLATE_CODE = "IN_PROGRESS_EXPERIMENT";
    private static final String FINISHED_EXPERIMENT_EMAIL_TEMPLATE_CODE = "FINISHED_EXPERIMENT";
    private static final String ERROR_EXPERIMENT_EMAIL_TEMPLATE_CODE = "ERROR_EXPERIMENT";
    private static final String REPLY_YO = "reply-yo";
    private static final String CREATED_BY = "user";

    @Inject
    private ExperimentRepository experimentRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @Inject
    private ExperimentConfig experimentConfig;
    @Inject
    private RuntimeService runtimeService;

    @Inject
    private ExperimentProcessManager experimentProcessManager;

    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<AbstractPushRequest> pushRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<EvaluationResultsRequest> evaluationResultsRequestArgumentCaptor;

    @Test
    void testCreateExperimentWebRequest() {
        var experimentWebRequest = createExperimentWebRequest();
        experimentWebRequest.setCreatedBy(CREATED_BY);
        experimentProcessManager.createExperimentRequest(experimentWebRequest);
        verify(getEmailClient(), atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(1);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(2);

        var experiment = getExperiment(experimentWebRequest.getRequestId());

        verifyTestSteps(experiment,
                new EvaluationRequestStatusVerifier(RequestStatus.NEW),
                new NewEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.NEW, 0),
                new PushRequestVerifier(PushType.USER_NOTIFICATION, RequestStatus.NEW, 1)
        );
    }

    @Test
    void testCreateExperimentRequest() {
        ExperimentMessageRequestData experimentMessageRequestData = createExperimentMessageRequest();
        experimentProcessManager.createExperimentRequest(experimentMessageRequestData);
        verify(getEmailClient(), atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(1);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(1);

        var experiment = getExperiment(experimentMessageRequestData.getRequestId());

        verifyTestSteps(experiment,
                new EvaluationRequestStatusVerifier(RequestStatus.NEW),
                new NewEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.NEW, 0)
        );
    }

    @Test
    void testProcessExperimentWithQueueChannel() {
        Experiment experiment = createAndSaveExperiment(Channel.QUEUE);
        testProcessExperiment(experiment);

        var actualExperiment = getExperiment(experiment.getRequestId());

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(2);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(2);

        verifyTestSteps(actualExperiment,
                new EvaluationRequestStatusVerifier(RequestStatus.FINISHED),
                new InProgressEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.IN_PROGRESS, 0),
                new FinishedEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.FINISHED, 1),
                new EvaluationResultsRequestsVerifier()
        );
    }

    @Test
    void testProcessExperimentWithWebChannel() {
        Experiment experiment = createAndSaveExperiment(Channel.WEB);
        testProcessExperiment(experiment);

        var actualExperiment = getExperiment(experiment.getRequestId());

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(2);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(4);

        verifyTestSteps(actualExperiment,
                new EvaluationRequestStatusVerifier(RequestStatus.FINISHED),
                new InProgressEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.IN_PROGRESS, 0),
                new PushRequestVerifier(PushType.USER_NOTIFICATION, RequestStatus.IN_PROGRESS, 1),
                new FinishedEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.FINISHED, 2),
                new PushRequestVerifier(PushType.USER_NOTIFICATION, RequestStatus.FINISHED, 3),
                new EvaluationResultsRequestsVerifier()
        );
    }

    @Test
    void testProcessErrorExperimentWithQueueChannel() throws IOException {
        Experiment experiment = createAndSaveExperiment(Channel.QUEUE);
        doThrow(IOException.class).when(getObjectStorageService()).uploadObject(any(Serializable.class), anyString());
        testProcessExperiment(experiment);

        var actualExperiment = getExperiment(experiment.getRequestId());

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(2);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(2);

        verifyTestSteps(actualExperiment,
                new EvaluationRequestStatusVerifier(RequestStatus.ERROR),
                new InProgressEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.IN_PROGRESS, 0),
                new ErrorEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.ERROR, 1)
        );
    }

    private void testProcessExperiment(Experiment experiment) {
        experimentProcessManager.processExperiment(experiment.getId());

        await().timeout(Duration.ofSeconds(PROCESS_EXPERIMENT_TIMEOUT_SECONDS))
                .until(() -> hasNoActiveProcess(experiment));

        verify(getEmailClient(), atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());
        verify(getErsClient(), atLeastOnce()).save(evaluationResultsRequestArgumentCaptor.capture());
    }

    private Experiment getExperiment(String requestId) {
        return experimentRepository.findByRequestId(requestId).orElse(null);
    }

    private Experiment createAndSaveExperiment(Channel channel) {
        Experiment experiment = createExperiment(UUID.randomUUID().toString(), RequestStatus.NEW);
        experiment.setChannel(channel);
        channel.visit(new ChannelVisitor() {
            @Override
            public void visitWeb() {
                experiment.setCreatedBy(CREATED_BY);
            }

            @Override
            public void visitQueue() {
                experiment.setCorrelationId(UUID.randomUUID().toString());
                experiment.setReplyTo(REPLY_YO);
            }
        });
        instancesInfoRepository.save(experiment.getInstancesInfo());
        return experimentRepository.save(experiment);
    }

    private boolean hasNoActiveProcess(Experiment experiment) {
        var activeProcessInstancesCount = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(experiment.getRequestId())
                .active()
                .count();
        return activeProcessInstancesCount == 0L;
    }

    @RequiredArgsConstructor
    private abstract class AbstractEmailRequestVerifier implements TestStepVerifier<Experiment> {

        private final String expectedTemplateCode;
        private final int order;

        @Override
        public void verifyStep(Experiment experiment) {
            EmailRequest emailRequest = emailRequestArgumentCaptor.getAllValues().get(order);
            assertThat(emailRequest).isNotNull();
            assertThat(emailRequest.getTemplateCode()).isEqualTo(expectedTemplateCode);
            assertThat(emailRequest.getReceiver()).isEqualTo(experiment.getEmail());
            assertThat(emailRequest.getVariables()).isNotEmpty();
            assertThat(emailRequest.getVariables()).containsEntry(
                    ExperimentEmailTemplateVariable.EXPERIMENT_TYPE.getVariableName(),
                    experiment.getExperimentType().getDescription()
            );
            assertThat(emailRequest.getVariables()).containsEntry(
                    ExperimentEmailTemplateVariable.REQUEST_ID.getVariableName(),
                    experiment.getRequestId()
            );
            verifySpecificVariables(emailRequest, experiment);
        }

        void verifySpecificVariables(EmailRequest emailRequest, Experiment experiment) {
        }
    }

    private class NewEmailRequestVerifier extends AbstractEmailRequestVerifier {

        public NewEmailRequestVerifier() {
            super(NEW_EXPERIMENT_EMAIL_TEMPLATE_CODE, 0);
        }
    }

    private class InProgressEmailRequestVerifier extends AbstractEmailRequestVerifier {

        public InProgressEmailRequestVerifier() {
            super(IN_PROGRESS_EXPERIMENT_EMAIL_TEMPLATE_CODE, 0);
        }
    }

    private class FinishedEmailRequestVerifier extends AbstractEmailRequestVerifier {

        public FinishedEmailRequestVerifier() {
            super(FINISHED_EXPERIMENT_EMAIL_TEMPLATE_CODE, 1);
        }

        @Override
        void verifySpecificVariables(EmailRequest emailRequest, Experiment experiment) {
            assertThat(emailRequest.getVariables()).containsEntry(
                    ExperimentEmailTemplateVariable.DOWNLOAD_URL.getVariableName(),
                    experiment.getExperimentDownloadUrl()
            );
        }
    }

    private class ErrorEmailRequestVerifier extends AbstractEmailRequestVerifier {

        public ErrorEmailRequestVerifier() {
            super(ERROR_EXPERIMENT_EMAIL_TEMPLATE_CODE, 1);
        }
    }

    @RequiredArgsConstructor
    private class PushRequestVerifier implements TestStepVerifier<Experiment> {

        private final PushType expectedPushType;
        private final RequestStatus expectedRequestStatusProperty;
        private final int order;

        @Override
        public void verifyStep(Experiment experiment) {
            AbstractPushRequest pushRequest = pushRequestArgumentCaptor.getAllValues().get(order);
            assertThat(pushRequest).isNotNull();
            assertThat(pushRequest.getPushType()).isEqualTo(expectedPushType);
            if (PushType.USER_NOTIFICATION.equals(pushRequest.getPushType())) {
                verifyReceiver(pushRequest, experiment);
            }
            assertThat(pushRequest.getMessageType()).isEqualTo(PUSH_MESSAGE_TYPE);
            assertThat(pushRequest.getAdditionalProperties()).isNotEmpty();
            assertThat(pushRequest.getAdditionalProperties()).containsEntry(EVALUATION_ID,
                    String.valueOf(experiment.getId()));
            assertThat(pushRequest.getAdditionalProperties()).containsEntry(EVALUATION_REQUEST_ID,
                    experiment.getRequestId()
            );
            assertThat(pushRequest.getAdditionalProperties()).containsEntry(EVALUATION_REQUEST_STATUS,
                    expectedRequestStatusProperty.name()
            );
        }

        private void verifyReceiver(AbstractPushRequest pushRequest, Experiment experiment) {
            UserPushNotificationRequest userPushNotificationRequest = (UserPushNotificationRequest) pushRequest;
            assertThat(userPushNotificationRequest.getReceivers()).hasSize(1);
            assertThat(userPushNotificationRequest.getReceivers()).contains(experiment.getCreatedBy());
        }
    }

    private class EvaluationResultsRequestsVerifier implements TestStepVerifier<Experiment> {

        @Override
        public void verifyStep(Experiment experiment) {
            assertThat(evaluationResultsRequestArgumentCaptor.getAllValues()).hasSize(experimentConfig.getResultSize());
        }
    }
}
