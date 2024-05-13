package com.ecaservice.server.controller.web;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.dto.CreateEvaluationRequestDto;
import com.ecaservice.server.dto.CreateOptimalEvaluationRequestDto;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.evaluation.EvaluationLogDataService;
import com.ecaservice.server.service.evaluation.EvaluationRequestWebApiService;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.CreateEvaluationResponseDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.S3ContentResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.server.TestHelperUtils.TEST_UUID;
import static com.ecaservice.server.TestHelperUtils.buildEvaluationRequestDto;
import static com.ecaservice.server.TestHelperUtils.buildOptimalEvaluationRequestDto;
import static com.ecaservice.server.TestHelperUtils.createPageRequestDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link EvaluationController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = EvaluationController.class)
@Import({EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class, DateTimeConverter.class,
        ClassifierInfoMapperImpl.class})
class EvaluationControllerTest extends PageRequestControllerTest {

    private static final String BASE_URL = "/evaluation";
    private static final String DETAILS_URL = BASE_URL + "/details/{id}";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String CREATE_EVALUATION_URL = BASE_URL + "/create";

    private static final String CREATE_OPTIMAL_EVALUATION_URL = BASE_URL + "/create-optimal";
    private static final String REQUEST_STATUS_STATISTICS_URL = BASE_URL + "/request-statuses-statistics";
    private static final String CLASSIFIERS_STATISTICS_URL = BASE_URL + "/classifiers-statistics";
    private static final String MODEL_CONTENT_URL = BASE_URL + "/model/{id}";
    private static final String CONTENT_URL = "http://localhost:9000/content";
    private static final long ID = 1L;

    @MockBean
    private EvaluationLogDataService evaluationLogDataService;
    @MockBean
    private EvaluationRequestWebApiService evaluationRequestWebApiService;
    @MockBean
    private EvaluationLogRepository evaluationLogRepository;

    @Autowired
    private EvaluationLogMapper evaluationLogMapper;

    @Test
    void testCreateEvaluationUnauthorized() throws Exception {
        var createEvaluationRequestDto = buildEvaluationRequestDto();
        mockMvc.perform(post(CREATE_EVALUATION_URL)
                        .content(objectMapper.writeValueAsString(createEvaluationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateEvaluationSuccess() throws Exception {
        var createEvaluationRequestDto = buildEvaluationRequestDto();
        CreateEvaluationResponseDto expected = CreateEvaluationResponseDto.builder()
                .id(ID)
                .requestId(UUID.randomUUID().toString())
                .build();
        when(evaluationRequestWebApiService.createEvaluationRequest(any(CreateEvaluationRequestDto.class)))
                .thenReturn(expected);
        mockMvc.perform(post(CREATE_EVALUATION_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(createEvaluationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testCreateOptimalEvaluationUnauthorized() throws Exception {
        var createEvaluationRequestDto = buildOptimalEvaluationRequestDto();
        mockMvc.perform(post(CREATE_OPTIMAL_EVALUATION_URL)
                        .content(objectMapper.writeValueAsString(createEvaluationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateOptimalEvaluationSuccess() throws Exception {
        var createEvaluationRequestDto = buildOptimalEvaluationRequestDto();
        CreateEvaluationResponseDto expected = CreateEvaluationResponseDto.builder()
                .id(ID)
                .requestId(UUID.randomUUID().toString())
                .build();
        when(evaluationRequestWebApiService.createOptimalEvaluationRequest(any(CreateOptimalEvaluationRequestDto.class)))
                .thenReturn(expected);
        mockMvc.perform(post(CREATE_OPTIMAL_EVALUATION_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(createEvaluationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void testCreateEvaluationBadRequestWithNullClassifierOptions() throws Exception {
        var evaluationRequestDto = buildEvaluationRequestDto();
        evaluationRequestDto.setClassifierOptions(null);
        internalTestCreateEvaluationBadRequest(evaluationRequestDto);
    }

    @Test
    void testCreateEvaluationBadRequestWithNullEvaluationMethod() throws Exception {
        var evaluationRequestDto = buildEvaluationRequestDto();
        evaluationRequestDto.setEvaluationMethod(null);
        internalTestCreateEvaluationBadRequest(evaluationRequestDto);
    }

    @Test
    void testCreateExperimentBadRequestWithEmptyInstancesUuid() throws Exception {
        var evaluationRequestDto = buildEvaluationRequestDto();
        evaluationRequestDto.setInstancesUuid(StringUtils.EMPTY);
        internalTestCreateEvaluationBadRequest(evaluationRequestDto);
    }

    @Test
    void testGetEvaluationLogDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAILS_URL, ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEvaluationLogDetailsNotFound() throws Exception {
        when(evaluationLogRepository.findById(ID)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetEvaluationLogDetailsOk() throws Exception {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog(TEST_UUID, RequestStatus.FINISHED);
        when(evaluationLogRepository.findById(ID)).thenReturn(Optional.of(evaluationLog));
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogMapper.mapDetails(evaluationLog);
        when(evaluationLogDataService.getEvaluationLogDetails(evaluationLog)).thenReturn(evaluationLogDetailsDto);
        mockMvc.perform(get(DETAILS_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(evaluationLogDetailsDto)));
    }

    @Test
    void testGetEvaluationLogsUnauthorized() throws Exception {
        testGetPageUnauthorized(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetEvaluationLogsWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetEvaluationLogsWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetEvaluationLogsWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetEvaluationLogsWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetEvaluationLogsWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetEvaluationLogsWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(LIST_URL, Collections.emptyMap());
    }

    @Test
    void testGetEvaluationLogsOk() throws Exception {
        var evaluationLogs = Collections.singletonList(TestHelperUtils.createEvaluationLog());
        var evaluationLogsDtoList = evaluationLogs
                .stream()
                .map(evaluationLogMapper::map)
                .collect(Collectors.toList());
        var pageDto = PageDto.of(evaluationLogsDtoList, PAGE_NUMBER, TOTAL_ELEMENTS);
        when(evaluationLogDataService.getEvaluationLogsPage(any(PageRequestDto.class))).thenReturn(pageDto);
        mockMvc.perform(post(LIST_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(createPageRequestDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(pageDto)));
    }

    @Test
    void testEvaluationRequestStatusesStatisticsUnauthorized() throws Exception {
        mockMvc.perform(get(REQUEST_STATUS_STATISTICS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testEvaluationRequestStatusesStatisticsOk() throws Exception {
        RequestStatusStatisticsDto requestStatusStatisticsDto = new RequestStatusStatisticsDto();
        when(evaluationLogDataService.getRequestStatusesStatistics()).thenReturn(requestStatusStatisticsDto);
        mockMvc.perform(get(REQUEST_STATUS_STATISTICS_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(requestStatusStatisticsDto)));
    }

    @Test
    void testGetClassifiersStatisticsUnauthorized() throws Exception {
        mockMvc.perform(get(CLASSIFIERS_STATISTICS_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void testGetClassifiersStatisticsOk() throws Exception {
        ChartDto chartDto = ChartDto.builder()
                .total(1L)
                .dataItems(Collections.singletonList(new ChartDataDto("Item", "Item", 1L)))
                .build();
        when(evaluationLogDataService.getClassifiersStatisticsData(null, null))
                .thenReturn(chartDto);
        mockMvc.perform(get(CLASSIFIERS_STATISTICS_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(chartDto)));
    }

    @Test
    void testGetClassifierModelContentUrlUnauthorized() throws Exception {
        mockMvc.perform(get(MODEL_CONTENT_URL, ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetModelContentUrlForNotExistingEvaluationLog() throws Exception {
        when(evaluationLogDataService.getModelContentUrl(ID)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(MODEL_CONTENT_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetModelContentUrlOk() throws Exception {
        var s3ContentResponseDto = S3ContentResponseDto.builder()
                .contentUrl(CONTENT_URL)
                .build();
        when(evaluationLogDataService.getModelContentUrl(ID)).thenReturn(s3ContentResponseDto);
        mockMvc.perform(get(MODEL_CONTENT_URL, ID)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(s3ContentResponseDto)));
    }

    private void internalTestCreateEvaluationBadRequest(CreateEvaluationRequestDto evaluationRequestDto)
            throws Exception {
        mockMvc.perform(post(CREATE_EVALUATION_URL)
                        .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                        .content(objectMapper.writeValueAsString(evaluationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
