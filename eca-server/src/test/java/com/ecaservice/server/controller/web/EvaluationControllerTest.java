package com.ecaservice.server.controller.web;

import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.server.TestHelperUtils;
import com.ecaservice.server.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.server.mapping.DateTimeConverter;
import com.ecaservice.server.mapping.EvaluationLogMapper;
import com.ecaservice.server.mapping.EvaluationLogMapperImpl;
import com.ecaservice.server.mapping.InstancesInfoMapperImpl;
import com.ecaservice.server.model.entity.EvaluationLog;
import com.ecaservice.server.model.entity.RequestStatus;
import com.ecaservice.server.repository.EvaluationLogRepository;
import com.ecaservice.server.service.evaluation.EvaluationLogService;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.ChartDto;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecaservice.server.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.server.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.server.TestHelperUtils.TEST_UUID;
import static com.ecaservice.server.TestHelperUtils.bearerHeader;
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
    private static final String REQUEST_STATUS_STATISTICS_URL = BASE_URL + "/request-statuses-statistics";
    private static final String CLASSIFIERS_STATISTICS_URL = BASE_URL + "/classifiers-statistics";
    private static final long ID = 1L;

    @MockBean
    private EvaluationLogService evaluationLogService;
    @MockBean
    private EvaluationLogRepository evaluationLogRepository;

    @Inject
    private EvaluationLogMapper evaluationLogMapper;

    @Test
    void testGetEvaluationLogDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAILS_URL, ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEvaluationLogDetailsNotFound() throws Exception {
        when(evaluationLogRepository.findById(ID)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetEvaluationLogDetailsOk() throws Exception {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog(TEST_UUID, RequestStatus.FINISHED);
        when(evaluationLogRepository.findById(ID)).thenReturn(Optional.of(evaluationLog));
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogMapper.mapDetails(evaluationLog);
        when(evaluationLogService.getEvaluationLogDetails(evaluationLog)).thenReturn(evaluationLogDetailsDto);
        mockMvc.perform(get(DETAILS_URL, ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
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
        when(evaluationLogService.getEvaluationLogsPage(any(PageRequestDto.class))).thenReturn(pageDto);
        mockMvc.perform(post(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
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
        when(evaluationLogService.getRequestStatusesStatistics()).thenReturn(requestStatusStatisticsDto);
        mockMvc.perform(get(REQUEST_STATUS_STATISTICS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
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
        when(evaluationLogService.getClassifiersStatisticsData(null, null))
                .thenReturn(chartDto);
        mockMvc.perform(get(CLASSIFIERS_STATISTICS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(chartDto)));

    }
}
