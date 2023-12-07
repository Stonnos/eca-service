package com.ecaservice.server.service;

import com.ecaservice.core.mail.client.service.EmailClient;
import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.s3.client.minio.model.GetPresignedUrlObject;
import com.ecaservice.s3.client.minio.service.ObjectStorageService;
import com.ecaservice.server.model.data.InstancesMetaDataModel;
import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import com.ecaservice.server.service.auth.UsersClient;
import com.ecaservice.server.service.data.InstancesLoaderService;
import com.ecaservice.server.service.data.InstancesMetaDataService;
import com.ecaservice.server.service.ds.DataStorageService;
import com.ecaservice.server.service.ers.ErsClient;
import com.ecaservice.server.service.push.WebPushClient;
import com.ecaservice.server.verifier.TestStepVerifier;
import com.ecaservice.user.dto.UserInfoDto;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import weka.core.Instances;

import java.util.UUID;

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
    private static final String DATA_MD_5_HASH = "3032e188204cb537f69fc7364f638641";
    private static final String CREATED_BY = "user";
    private static final String TEST_MAIL_RU = "test@mail.ru";

    @MockBean
    @Getter
    private EmailClient emailClient;
    @MockBean
    @Getter
    private WebPushClient webPushClient;
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

    @BeforeEach
    void init() {
        mockLoadInstances();
        mockGetDownloadUrl();
        mockSentEvaluationResults();
        mockGetUserInfo();
        mockExportValidInstances();
        mockSentEmail();
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
        var instancesDataModel = new InstancesMetaDataModel(data.relationName(), data.numInstances(),
                data.numAttributes(), data.numClasses(), data.classAttribute().name(), DATA_MD_5_HASH,
                "instances.json");
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

    private void mockSentEmail() {
        when(emailClient.sendEmail(any(EmailRequest.class))).thenReturn(new EmailResponse());
    }

    private void mockExportValidInstances() {
        ExportInstancesResponseDto exportInstancesResponseDto = new ExportInstancesResponseDto();
        exportInstancesResponseDto.setExternalDataUuid(UUID.randomUUID().toString());
        when(dataStorageService.exportValidInstances(anyString())).thenReturn(exportInstancesResponseDto);
    }
}
