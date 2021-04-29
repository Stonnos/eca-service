package com.ecaservice.external.api.controller;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.RequestStatus;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.entity.EvaluationRequestEntity;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.metrics.MetricsService;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.service.EcaRequestService;
import com.ecaservice.external.api.service.EvaluationApiService;
import com.ecaservice.external.api.service.InstancesService;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationRequestEntity;
import static com.ecaservice.external.api.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.external.api.TestHelperUtils.createInstancesMockMultipartFile;
import static com.ecaservice.external.api.util.Constants.DATA_URL_PREFIX;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ExternalApiController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = ExternalApiController.class)
class ExternalApiControllerTest extends AbstractControllerTest {

    private static final String BASE_URL = "/";
    private static final String UPLOAD_DATA_URL = BASE_URL + "uploads-train-data";
    private static final String DOWNLOAD_MODEL_URL = BASE_URL + "download-model/{requestId}";

    @MockBean
    private ExternalApiConfig externalApiConfig;
    @MockBean
    private MessageCorrelationService messageCorrelationService;
    @MockBean
    private TimeoutFallback timeoutFallback;
    @MockBean
    private MetricsService metricsService;
    @MockBean
    private EvaluationApiService evaluationApiService;
    @MockBean
    private EcaRequestService ecaRequestService;
    @MockBean
    private InstancesService instancesService;
    @MockBean
    private EcaRequestRepository ecaRequestRepository;
    @MockBean
    private EvaluationRequestRepository evaluationRequestRepository;

    @Test
    void testUploadInstancesUnauthorized() throws Exception {
        MockMultipartFile trainingData = createInstancesMockMultipartFile();
        mockMvc.perform(multipart(UPLOAD_DATA_URL)
                .file(trainingData))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUploadInstances() throws Exception {
        MockMultipartFile trainingData = createInstancesMockMultipartFile();
        InstancesEntity instancesEntity = createInstancesEntity(LocalDateTime.now());
        when(instancesService.uploadInstances(trainingData)).thenReturn(instancesEntity);
        InstancesDto instancesDto = new InstancesDto(instancesEntity.getUuid(),
                String.format("%s%s", DATA_URL_PREFIX, instancesEntity.getUuid()));
        ResponseDto<InstancesDto> expected = ResponseDto.<InstancesDto>builder()
                .requestStatus(RequestStatus.SUCCESS)
                .payload(instancesDto)
                .build();
        mockMvc.perform(multipart(UPLOAD_DATA_URL)
                .file(trainingData)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testDownloadModelUnauthorized() throws Exception {
        mockMvc.perform(get(DOWNLOAD_MODEL_URL, UUID.randomUUID().toString())).andExpect(status().isUnauthorized());
    }

    @Test
    void testDownloadModelForNotExistingRequest() throws Exception {
        String requestId = UUID.randomUUID().toString();
        when(ecaRequestService.getById(requestId)).thenThrow(
                new EntityNotFoundException(EvaluationRequestEntity.class, requestId));
        mockMvc.perform(get(DOWNLOAD_MODEL_URL, requestId)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDownloadModelWithNotExistingFile() throws Exception {
        EvaluationRequestEntity evaluationRequestEntity = createEvaluationRequestEntity(UUID.randomUUID().toString());
        evaluationRequestEntity.setClassifierAbsolutePath(null);
        when(ecaRequestService.getById(evaluationRequestEntity.getCorrelationId())).thenReturn(evaluationRequestEntity);
        mockMvc.perform(get(DOWNLOAD_MODEL_URL, evaluationRequestEntity.getCorrelationId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }
}
