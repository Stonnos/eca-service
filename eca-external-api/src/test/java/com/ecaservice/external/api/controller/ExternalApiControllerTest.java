package com.ecaservice.external.api.controller;

import com.ecaservice.external.api.config.ExternalApiConfig;
import com.ecaservice.external.api.dto.EvaluationStatus;
import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.ResponseCode;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.entity.InstancesEntity;
import com.ecaservice.external.api.metrics.MetricsService;
import com.ecaservice.external.api.repository.EcaRequestRepository;
import com.ecaservice.external.api.repository.EvaluationRequestRepository;
import com.ecaservice.external.api.service.EcaRequestService;
import com.ecaservice.external.api.service.EvaluationApiService;
import com.ecaservice.external.api.service.EvaluationResultsResponseService;
import com.ecaservice.external.api.service.InstancesService;
import com.ecaservice.external.api.service.MessageCorrelationService;
import com.ecaservice.oauth2.test.controller.AbstractControllerTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.ecaservice.external.api.TestHelperUtils.createEvaluationResponseDto;
import static com.ecaservice.external.api.TestHelperUtils.createExperimentResponseDto;
import static com.ecaservice.external.api.TestHelperUtils.createInstancesEntity;
import static com.ecaservice.external.api.TestHelperUtils.createInstancesMockMultipartFile;
import static com.ecaservice.external.api.util.Constants.DATA_URL_PREFIX;
import static com.ecaservice.external.api.util.Utils.buildResponse;
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
    private static final String EVALUATION_RESULTS_URL = BASE_URL + "evaluation-results/{requestId}";
    private static final String EXPERIMENT_RESULTS_URL = BASE_URL + "experiment-results/{requestId}";

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
    private EvaluationResultsResponseService evaluationResultsResponseService;
    @MockBean
    private EcaRequestService ecaRequestService;
    @MockBean
    private InstancesService instancesService;
    @MockBean
    private EcaRequestRepository ecaRequestRepository;
    @MockBean
    private EvaluationRequestRepository evaluationRequestRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
                .responseCode(ResponseCode.SUCCESS)
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
    void testGetEvaluationResultsUnauthorized() throws Exception {
        mockMvc.perform(get(EVALUATION_RESULTS_URL, UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEvaluationResultsSuccess() throws Exception {
        String correlationId = UUID.randomUUID().toString();
        var evaluationResponseDto = createEvaluationResponseDto(correlationId, EvaluationStatus.IN_PROGRESS);
        var expectedResponseDto = buildResponse(ResponseCode.SUCCESS, evaluationResponseDto);
        when(evaluationResultsResponseService.getEvaluationResultsResponse(correlationId)).thenReturn(
                evaluationResponseDto);
        mockMvc.perform(get(EVALUATION_RESULTS_URL, correlationId)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseDto)));
    }

    @Test
    void testGetExperimentResultsUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_RESULTS_URL, UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetExperimentResultsSuccess() throws Exception {
        String correlationId = UUID.randomUUID().toString();
        var experimentResponseDto = createExperimentResponseDto(correlationId, EvaluationStatus.IN_PROGRESS);
        var expectedResponseDto = buildResponse(ResponseCode.SUCCESS, experimentResponseDto);
        when(evaluationResultsResponseService.getExperimentResultsResponse(correlationId))
                .thenReturn(experimentResponseDto);
        mockMvc.perform(get(EXPERIMENT_RESULTS_URL, correlationId)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseDto)));
    }
}
