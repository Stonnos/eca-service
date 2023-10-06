package com.ecaservice.server.service.evaluation;

import com.ecaservice.base.model.EvaluationResponse;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.server.model.entity.Channel;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.repository.InstancesInfoRepository;
import com.ecaservice.server.service.AbstractEvaluationProcessManagerTest;
import com.ecaservice.server.verifier.EvaluationRequestStatusVerifier;
import com.ecaservice.server.verifier.TestStepVerifier;
import com.ecaservice.web.push.dto.AbstractPushRequest;
import com.ecaservice.web.push.dto.PushType;
import com.ecaservice.web.push.dto.UserPushNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import static com.ecaservice.server.TestHelperUtils.createEvaluationLog;
import static com.ecaservice.server.TestHelperUtils.createEvaluationMessageRequestModel;
import static com.ecaservice.server.TestHelperUtils.createEvaluationWebRequestModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

/**
 * Unit test for {@link EvaluationProcessManager} class.
 *
 * @author Roman Batygin
 */
@SpringBootTest
@Deployment(resources = {"bpmn/create-evaluation-web-request.bpmn", "bpmn/process-evaluation-web-request.bpmn",
        "bpmn/create-and-process-evaluation-message-request.bpmn", "bpmn/create-evaluation-request-process.bpmn"})
class EvaluationProcessManagerTest extends AbstractEvaluationProcessManagerTest<EvaluationLog> {

    private static final String PUSH_MESSAGE_TYPE = "EVALUATION_STATUS";
    private static final String EVALUATION_ID = "id";
    private static final String EVALUATION_REQUEST_ID = "requestId";
    private static final String EVALUATION_REQUEST_STATUS = "requestStatus";

    @Inject
    private EvaluationLogRepository evaluationLogRepository;
    @Inject
    private InstancesInfoRepository instancesInfoRepository;

    @Inject
    private EvaluationProcessManager evaluationProcessManager;

    @Captor
    private ArgumentCaptor<String> replyToCaptor;
    @Captor
    private ArgumentCaptor<MessagePostProcessor> messagePostProcessorArgumentCaptor;
    @Captor
    private ArgumentCaptor<EvaluationResponse> evaluationResponseArgumentCaptor;
    @Captor
    private ArgumentCaptor<AbstractPushRequest> pushRequestArgumentCaptor;
    @Captor
    private ArgumentCaptor<EvaluationResultsRequest> evaluationResultsRequestArgumentCaptor;

    @Test
    void testCreateEvaluationWebRequest() {
        var evaluationWebRequestModel = createEvaluationWebRequestModel();
        evaluationProcessManager.createAndProcessEvaluationRequest(evaluationWebRequestModel);
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());

        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(1);

        var evaluationLog = getEvaluationLog(evaluationWebRequestModel.getRequestId());

        verifyTestSteps(evaluationLog,
                new EvaluationRequestStatusVerifier(RequestStatus.NEW),
                new UserPushRequestVerifier(RequestStatus.NEW, 0)
        );
    }

    @Test
    void testCreateAndProcessEvaluationMessageRequest() {
        var evaluationMessageRequestModel = createEvaluationMessageRequestModel();

        evaluationProcessManager.createAndProcessEvaluationRequest(evaluationMessageRequestModel);

        var evaluationLog = getEvaluationLog(evaluationMessageRequestModel.getRequestId());

        verifyTestSteps(evaluationLog,
                new EvaluationRequestStatusVerifier(RequestStatus.FINISHED)
        );
    }

    @Test
    void testProcessEvaluationWebRequest() {
        EvaluationLog evaluationLog = createAndSaveEvaluationLog();
        testProcessEvaluationRequest(evaluationLog);
        verify(getErsClient(), atLeastOnce()).save(evaluationResultsRequestArgumentCaptor.capture());

        var actualEvaluationLog = getEvaluationLog(evaluationLog.getRequestId());

        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(2);

        verifyTestSteps(actualEvaluationLog,
                new EvaluationRequestStatusVerifier(RequestStatus.FINISHED),
                new UserPushRequestVerifier(RequestStatus.IN_PROGRESS, 0),
                new UserPushRequestVerifier(RequestStatus.FINISHED, 1),
                new EvaluationResultsRequestsVerifier()
        );
    }

    @Test
    void testProcessErrorEvaluationWebRequest() throws IOException {
        EvaluationLog evaluationLog = createAndSaveEvaluationLog();
        doThrow(IOException.class).when(getObjectStorageService()).uploadObject(any(Serializable.class), anyString());
        testProcessEvaluationRequest(evaluationLog);

        var actualEvaluationLog = getEvaluationLog(evaluationLog.getRequestId());

        assertThat(pushRequestArgumentCaptor.getAllValues()).hasSize(2);

        verifyTestSteps(actualEvaluationLog,
                new EvaluationRequestStatusVerifier(RequestStatus.ERROR),
                new UserPushRequestVerifier(RequestStatus.IN_PROGRESS, 0),
                new UserPushRequestVerifier(RequestStatus.ERROR, 1)
        );
    }

    private void testProcessEvaluationRequest(EvaluationLog evaluationLog) {
        evaluationProcessManager.processEvaluationRequest(evaluationLog.getId());
        verify(getWebPushClient(), atLeastOnce()).sendPush(pushRequestArgumentCaptor.capture());
    }

    private EvaluationLog getEvaluationLog(String requestId) {
        return evaluationLogRepository.findByRequestId(requestId).orElse(null);
    }

    private EvaluationLog createAndSaveEvaluationLog() {
        var evaluationLog = createEvaluationLog(UUID.randomUUID().toString(), RequestStatus.NEW);
        evaluationLog.setChannel(Channel.WEB);
        instancesInfoRepository.save(evaluationLog.getInstancesInfo());
        return evaluationLogRepository.save(evaluationLog);
    }

    @RequiredArgsConstructor
    private class UserPushRequestVerifier implements TestStepVerifier<EvaluationLog> {

        private final RequestStatus expectedRequestStatusProperty;
        private final int order;

        @Override
        public void verifyStep(EvaluationLog evaluationLog) {
            AbstractPushRequest pushRequest = pushRequestArgumentCaptor.getAllValues().get(order);
            assertThat(pushRequest).isNotNull();
            assertThat(pushRequest.getPushType()).isEqualTo(PushType.USER_NOTIFICATION);
            assertThat(pushRequest).isInstanceOf(UserPushNotificationRequest.class);
            UserPushNotificationRequest userPushNotificationRequest = (UserPushNotificationRequest) pushRequest;
            assertThat(userPushNotificationRequest.getReceivers()).hasSize(1);
            assertThat(userPushNotificationRequest.getReceivers()).contains(evaluationLog.getCreatedBy());
            assertThat(pushRequest.getMessageType()).isEqualTo(PUSH_MESSAGE_TYPE);
            assertThat(pushRequest.getAdditionalProperties()).isNotEmpty();
            assertThat(pushRequest.getAdditionalProperties()).containsEntry(EVALUATION_ID,
                    String.valueOf(evaluationLog.getId()));
            assertThat(pushRequest.getAdditionalProperties()).containsEntry(EVALUATION_REQUEST_ID,
                    evaluationLog.getRequestId()
            );
            assertThat(pushRequest.getAdditionalProperties()).containsEntry(EVALUATION_REQUEST_STATUS,
                    expectedRequestStatusProperty.name()
            );
        }
    }

    private class EvaluationResultsRequestsVerifier implements TestStepVerifier<EvaluationLog> {

        @Override
        public void verifyStep(EvaluationLog evaluationLog) {
            assertThat(evaluationResultsRequestArgumentCaptor.getAllValues()).hasSize(1);
        }
    }
}
