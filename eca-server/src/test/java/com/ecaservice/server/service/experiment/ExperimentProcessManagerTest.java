package com.ecaservice.server.service.experiment;

import com.ecaservice.base.model.EcaResponse;
import com.ecaservice.base.model.ExperimentResponse;
import com.ecaservice.base.model.TechnicalStatus;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.s3.client.minio.model.UploadObject;
import com.ecaservice.s3.client.minio.service.MinioStorageService;
import com.ecaservice.server.config.ExperimentConfig;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.ChannelVisitor;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.model.entity.RequestStatus;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createExperiment;
import static com.ecaservice.server.TestHelperUtils.createExperimentMessageRequestModel;
import static com.ecaservice.server.TestHelperUtils.createExperimentWebRequestModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link ExperimentProcessManager} class.
 *
 * @author Roman Batygin
 */
@Disabled
@SpringBootTest
@Deployment(resources = {"bpmn/process-experiment.bpmn", "bpmn/finish-experiment.bpmn",
        "bpmn/create-experiment-request-process.bpmn", "bpmn/create-experiment-web-request-process.bpmn",
        "bpmn/create-experiment-message-request-process.bpmn", "bpmn/send-final-experiment-email.bpmn",
        "bpmn/send-final-experiment-push.bpmn"})
class ExperimentProcessManagerTest extends AbstractEvaluationProcessManagerTest<Experiment> {

    private static final String PUSH_MESSAGE_TYPE = "EXPERIMENT_STATUS";
    private static final String EVALUATION_ID = "id";
    private static final String EVALUATION_REQUEST_ID = "requestId";
    private static final String EVALUATION_REQUEST_STATUS = "requestStatus";
    private static final String NEW_EXPERIMENT_EMAIL_TEMPLATE_CODE = "NEW_EXPERIMENT";
    private static final String IN_PROGRESS_EXPERIMENT_EMAIL_TEMPLATE_CODE = "IN_PROGRESS_EXPERIMENT";
    private static final String FINISHED_EXPERIMENT_EMAIL_TEMPLATE_CODE = "FINISHED_EXPERIMENT";
    private static final String ERROR_EXPERIMENT_EMAIL_TEMPLATE_CODE = "ERROR_EXPERIMENT";
    private static final String REPLY_TO = "reply-yo";
    private static final String CREATED_BY = "user";

    @MockBean
    private ExperimentModelLocalStorage experimentModelLocalStorage;
    @MockBean
    private MinioStorageService minioStorageService;

    @Autowired
    private ExperimentRepository experimentRepository;
    @Autowired
    private InstancesInfoRepository instancesInfoRepository;

    @Autowired
    private ExperimentConfig experimentConfig;
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ExperimentProcessManager experimentProcessManager;

    @Captor
    private ArgumentCaptor<EmailRequest> emailRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<AbstractPushRequest> pushRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<EvaluationResultsRequest> evaluationResultsRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;
    @Captor
    private ArgumentCaptor<String> correlationIdCaptor;
    @Captor
    private ArgumentCaptor<EcaResponse> ecaResponseArgumentCaptor;

    @Test
    void testCreateExperimentWebRequest() {
        var experimentRequestModel = createExperimentWebRequestModel();
        experimentProcessManager.createExperimentRequest(experimentRequestModel);
        verify(getEmailClient(), atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(1);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(2);

        var experiment = getExperiment(experimentRequestModel.getRequestId());

        verifyTestSteps(experiment,
                new EvaluationRequestStatusVerifier(RequestStatus.NEW),
                new NewEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.NEW, 0),
                new PushRequestVerifier(PushType.USER_NOTIFICATION, RequestStatus.NEW, 1)
        );
    }

    @Test
    void testCreateExperimentWebRequestWithDisabledNotifications() {
        var experimentRequestModel = createExperimentWebRequestModel();
        mockGetUserProfileOptions(false);
        experimentProcessManager.createExperimentRequest(experimentRequestModel);
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());

        verify(getEmailClient(), never()).sendEmail(any(EmailRequest.class));
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(1);

        var experiment = getExperiment(experimentRequestModel.getRequestId());

        verifyTestSteps(experiment,
                new EvaluationRequestStatusVerifier(RequestStatus.NEW),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.NEW, 0)
        );
    }

    @Test
    void testCreateExperimentMessageRequest() {
        var experimentRequestModel = createExperimentMessageRequestModel();
        experimentProcessManager.createExperimentRequest(experimentRequestModel);
        verify(getEmailClient(), atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());
        captureEcaResponse();

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(1);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(1);

        var experiment = getExperiment(experimentRequestModel.getRequestId());

        verifyTestSteps(experiment,
                new EvaluationRequestStatusVerifier(RequestStatus.NEW),
                new NewEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.NEW, 0),
                new EcaResponseVerifier(experiment.getCorrelationId(), experiment.getReplyTo(),
                        TechnicalStatus.IN_PROGRESS)
        );
    }

    @Test
    void testProcessExperimentWithQueueChannel() {
        Experiment experiment = createAndSaveExperiment(Channel.QUEUE);
        testProcessExperiment(experiment);

        captureEcaResponse();

        var actualExperiment = getExperiment(experiment.getRequestId());

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(2);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(2);

        verifyTestSteps(actualExperiment,
                new EvaluationRequestStatusVerifier(RequestStatus.FINISHED),
                new InProgressEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.IN_PROGRESS, 0),
                new FinishedEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.FINISHED, 1),
                new EvaluationResultsRequestsVerifier(),
                new EcaResponseVerifier(experiment.getCorrelationId(), experiment.getReplyTo(), TechnicalStatus.SUCCESS)
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
    void testProcessExperimentWithWebChannelWithDisabledNotifications() {
        Experiment experiment = createAndSaveExperiment(Channel.WEB);
        mockGetUserProfileOptions(false);
        experimentProcessManager.processExperiment(experiment.getId());
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());
        verify(getErsClient(), atLeastOnce()).save(evaluationResultsRequestArgumentCaptor.capture());

        var actualExperiment = getExperiment(experiment.getRequestId());

        verify(getEmailClient(), never()).sendEmail(any(EmailRequest.class));
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(2);

        verifyTestSteps(actualExperiment,
                new EvaluationRequestStatusVerifier(RequestStatus.FINISHED),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.IN_PROGRESS, 0),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.FINISHED, 1),
                new EvaluationResultsRequestsVerifier()
        );
    }

    @Test
    void testProcessErrorExperimentWithQueueChannel() {
        Experiment experiment = createAndSaveExperiment(Channel.QUEUE);
        doThrow(RuntimeException.class).when(minioStorageService).uploadObject(any(UploadObject.class));
        testProcessExperiment(experiment);

        captureEcaResponse();

        var actualExperiment = getExperiment(experiment.getRequestId());

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(2);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(2);

        verifyTestSteps(actualExperiment,
                new EvaluationRequestStatusVerifier(RequestStatus.ERROR),
                new InProgressEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.IN_PROGRESS, 0),
                new ErrorEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.ERROR, 1),
                new EcaResponseVerifier(experiment.getCorrelationId(), experiment.getReplyTo(), TechnicalStatus.ERROR)
        );
    }

    @Test
    void testProcessErrorExperimentWithWebChannel() {
        Experiment experiment = createAndSaveExperiment(Channel.WEB);
        doThrow(RuntimeException.class).when(minioStorageService).uploadObject(any(UploadObject.class));
        testProcessExperiment(experiment);

        var actualExperiment = getExperiment(experiment.getRequestId());

        assertThat(emailRequestArgumentCaptor.getAllValues()).hasSize(2);
        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(4);

        verifyTestSteps(actualExperiment,
                new EvaluationRequestStatusVerifier(RequestStatus.ERROR),
                new InProgressEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.IN_PROGRESS, 0),
                new PushRequestVerifier(PushType.USER_NOTIFICATION, RequestStatus.IN_PROGRESS, 1),
                new ErrorEmailRequestVerifier(),
                new PushRequestVerifier(PushType.SYSTEM, RequestStatus.ERROR, 2),
                new PushRequestVerifier(PushType.USER_NOTIFICATION, RequestStatus.ERROR, 3)
        );
    }

    private void testProcessExperiment(Experiment experiment) {
        experimentProcessManager.processExperiment(experiment.getId());
        verify(getEmailClient(), atLeastOnce()).sendEmail(emailRequestArgumentCaptor.capture());
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());
        verify(getErsClient(), atLeastOnce()).save(evaluationResultsRequestArgumentCaptor.capture());
    }

    private Experiment getExperiment(String requestId) {
        return experimentRepository.findByRequestId(requestId).orElse(null);
    }

    private void captureEcaResponse() {
        verify(getEcaResponseSender()).sendResponse(ecaResponseArgumentCaptor.capture(),
                correlationIdCaptor.capture(), replyToCaptor.capture());
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
                experiment.setReplyTo(REPLY_TO);
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

    @RequiredArgsConstructor
    private class EcaResponseVerifier implements TestStepVerifier<Experiment> {

        private final String expectedCorrelationId;
        private final String expectedReplyTo;
        private final TechnicalStatus expectedStatus;

        @Override
        public void verifyStep(Experiment experiment) {
            assertThat(correlationIdCaptor.getValue()).isEqualTo(expectedCorrelationId);
            assertThat(replyToCaptor.getValue()).isEqualTo(expectedReplyTo);
            assertThat(ecaResponseArgumentCaptor.getValue()).isInstanceOf(ExperimentResponse.class);
            assertThat(ecaResponseArgumentCaptor.getValue().getStatus()).isEqualTo(expectedStatus);
            ExperimentResponse experimentResponse  = (ExperimentResponse) ecaResponseArgumentCaptor.getValue();
            assertThat(experiment.getExperimentDownloadUrl()).isEqualTo(experimentResponse.getDownloadUrl());
        }
    }
}
