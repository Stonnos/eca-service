package com.ecaservice.server.service;

import com.ecaservice.core.mail.client.config.rabbit.MailClientRabbitConfiguration;
import com.ecaservice.core.mail.client.service.EmailRequestSender;
import com.ecaservice.core.push.client.config.rabbit.WebPushClientRabbitConfiguration;
import com.ecaservice.core.push.client.service.WebPushSender;
import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.service.auth.UsersClient;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import com.ecaservice.server.service.ds.DataStorageService;
import com.ecaservice.server.service.ers.ErsClient;
import com.ecaservice.server.verifier.TestStepVerifier;
import com.ecaservice.user.dto.UserInfoDto;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsFeignClient;
import com.ecaservice.user.profile.options.client.service.UserProfileOptionsProvider;
import com.ecaservice.user.profile.options.dto.UserNotificationEventOptionsDto;
import com.ecaservice.user.profile.options.dto.UserNotificationEventType;
import com.ecaservice.user.profile.options.dto.UserProfileOptionsDto;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import weka.core.Instances;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ecaservice.server.TestHelperUtils.loadInstances;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit test for bpmn processes.
 *
 * @author Roman Batygin
 */
@TestPropertySource("classpath:application-camunda.properties")
@Sql("/sql/message-templates.sql")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractEvaluationProcessManagerTest<T extends AbstractEvaluationEntity> {

    private static final String MODEL_DOWNLOAD_URL = "http://localhost:8099/object-storage";
    private static final String CREATED_BY = "user";
    private static final String TEST_MAIL_RU = "test@mail.ru";

    @MockBean
    private MailClientRabbitConfiguration mailClientRabbitConfiguration;
    @MockBean
    private WebPushClientRabbitConfiguration webPushClientRabbitConfiguration;

    @MockBean
    @Getter
    private UserProfileOptionsFeignClient userProfileOptionsFeignClient;
    @MockBean
    @Getter
    private EmailRequestSender emailRequestSender;
    @MockBean
    @Getter
    private WebPushSender webPushSender;
    @MockBean
    @Getter
    private ObjectStorageService objectStorageService;
    @MockBean
    @Getter
    private ErsClient ersClient;
    @MockBean
    @Getter
    private UsersClient usersClient;
    @MockBean
    @Getter
    private DataStorageService dataStorageService;
    @MockBean
    @Getter
    private EcaResponseSender ecaResponseSender;
    @MockBean
    @Getter
    private InstancesLoaderService instancesLoaderService;
    @MockBean
    @Getter
    private InstancesMetaDataService instancesMetaDataService;
    @MockBean
    @Getter
    private UserService userService;
    @MockBean
    @Getter
    private UserProfileOptionsProvider userProfileOptionsProvider;

    private final String dataUuid = UUID.randomUUID().toString();

    @BeforeEach
    void init() {
        mockLoadInstances();
        mockGetDownloadUrl();
        mockSentEvaluationResults();
        mockGetUserInfo();
        mockExportValidInstances();
        mockGetUserProfileOptions(true);
        before();
    }

    public void before() {
    }

    @SafeVarargs
    public final void verifyTestSteps(T experiment, TestStepVerifier<? super T>... verifiers) {
        for (TestStepVerifier<? super T> verifier : verifiers) {
            verifier.verifyStep(experiment);
        }
    }

    private void mockLoadInstances() {
        Instances data = loadInstances();
        var instancesDataModel =
                new InstancesMetaDataModel(dataUuid, data.relationName(), data.numInstances(), data.numAttributes(),
                        data.numClasses(), data.classAttribute().name(), "instances.json", Collections.emptyList());
        when(instancesMetaDataService.getInstancesMetaData(anyString())).thenReturn(instancesDataModel);
        when(instancesLoaderService.loadInstances(anyString())).thenReturn(data);
    }

    private void mockGetDownloadUrl() {
        when(objectStorageService.getObjectPresignedProxyUrl(any(GetPresignedUrlObject.class)))
                .thenReturn(MODEL_DOWNLOAD_URL);
    }

    private void mockSentEvaluationResults() {
        when(ersClient.save(any(EvaluationResultsRequest.class))).thenReturn(new EvaluationResultsResponse());
    }

    private void mockGetUserInfo() {
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setLogin(CREATED_BY);
        userInfoDto.setEmail(TEST_MAIL_RU);
        when(userService.getCurrentUser()).thenReturn(CREATED_BY);
        when(usersClient.getUserInfo(CREATED_BY)).thenReturn(userInfoDto);
    }

    private void mockExportValidInstances() {
        ExportInstancesResponseDto exportInstancesResponseDto = new ExportInstancesResponseDto();
        exportInstancesResponseDto.setExternalDataUuid(UUID.randomUUID().toString());
        when(dataStorageService.exportValidInstances(anyString())).thenReturn(exportInstancesResponseDto);
    }

    protected void mockGetUserProfileOptions(boolean enabled) {
        UserProfileOptionsDto userProfileOptionsDto = new UserProfileOptionsDto();
        userProfileOptionsDto.setEmailEnabled(true);
        userProfileOptionsDto.setWebPushEnabled(true);
        var notificationEvents = Stream.of(UserNotificationEventType.values())
                .map(eventType -> {
                    var userNotificationEventOptionsDto = new UserNotificationEventOptionsDto();
                    userNotificationEventOptionsDto.setEventType(eventType);
                    userNotificationEventOptionsDto.setEmailEnabled(enabled);
                    userNotificationEventOptionsDto.setWebPushEnabled(enabled);
                    return userNotificationEventOptionsDto;
                })
                .collect(Collectors.toList());
        userProfileOptionsDto.setNotificationEventOptions(notificationEvents);
        when(userProfileOptionsProvider.getUserProfileOptions(anyString())).thenReturn(userProfileOptionsDto);
    }
}
