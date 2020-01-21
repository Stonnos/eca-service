package com.ecaservice.controller;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.configuation.annotation.Oauth2TestConfiguration;
import com.ecaservice.controller.web.EvaluationController;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.EvaluationLogMapperImpl;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationLog_;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.token.TokenService;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ecaservice.PageRequestUtils.FILTER_MATCH_MODE_PARAM;
import static com.ecaservice.PageRequestUtils.FILTER_NAME_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE;
import static com.ecaservice.PageRequestUtils.PAGE_PARAM;
import static com.ecaservice.PageRequestUtils.SIZE;
import static com.ecaservice.PageRequestUtils.SIZE_PARAM;
import static com.ecaservice.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.TestHelperUtils.TEST_UUID;
import static com.ecaservice.TestHelperUtils.bearerHeader;
import static com.ecaservice.TestHelperUtils.buildRequestStatusStatisticsMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for cheking {@link EvaluationController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = EvaluationController.class)
@Oauth2TestConfiguration
@Import({EvaluationLogMapperImpl.class, InstancesInfoMapperImpl.class,
        ClassifierInputOptionsMapperImpl.class, ClassifierInfoMapperImpl.class})
public class EvaluationControllerTest {

    private static final String BASE_URL = "/evaluation";
    private static final String DETAILS_URL = BASE_URL + "/details/{requestId}";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String REQUEST_STATUS_STATISTICS_URL = BASE_URL + "/request-statuses-statistics";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private EvaluationLogService evaluationLogService;
    @MockBean
    private EvaluationLogRepository evaluationLogRepository;

    @Inject
    private EvaluationLogMapper evaluationLogMapper;
    @Inject
    private TokenService tokenService;
    @Inject
    private MockMvc mockMvc;

    private String accessToken;

    @Before
    public void init() throws Exception {
        accessToken = tokenService.obtainAccessToken();
    }

    @Test
    public void testGetEvaluationLogDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAILS_URL, TEST_UUID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetEvaluationLogDetailsBadRequest() throws Exception {
        when(evaluationLogRepository.findByRequestId(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEvaluationLogDetailsOk() throws Exception {
        EvaluationLog evaluationLog = TestHelperUtils.createEvaluationLog(TEST_UUID, RequestStatus.FINISHED);
        when(evaluationLogRepository.findByRequestId(TEST_UUID)).thenReturn(evaluationLog);
        EvaluationLogDetailsDto evaluationLogDetailsDto = evaluationLogMapper.mapDetails(evaluationLog);
        when(evaluationLogService.getEvaluationLogDetails(evaluationLog)).thenReturn(evaluationLogDetailsDto);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(evaluationLogDetailsDto)));
    }

    @Test
    public void testGetEvaluationLogsUnauthorized() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .param(PAGE_PARAM, String.valueOf(PAGE))
                .param(SIZE_PARAM, String.valueOf(SIZE)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetEvaluationLogsWithNullPageNumber() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(SIZE_PARAM, String.valueOf(SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEvaluationLogsWithNullPageSize() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_PARAM, String.valueOf(PAGE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEvaluationLogsWithEmptyFilterRequestName() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_PARAM, String.valueOf(PAGE))
                .param(SIZE_PARAM, String.valueOf(SIZE))
                .param(FILTER_NAME_PARAM, StringUtils.EMPTY)
                .param(FILTER_MATCH_MODE_PARAM, MatchMode.RANGE.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEvaluationLogsWithNullMatchMode() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_PARAM, String.valueOf(PAGE))
                .param(SIZE_PARAM, String.valueOf(SIZE))
                .param(FILTER_NAME_PARAM, EvaluationLog_.CREATION_DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEvaluationLogsOk() throws Exception {
        Page<EvaluationLog> evaluationLogPage = Mockito.mock(Page.class);
        when(evaluationLogPage.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        List<EvaluationLog> evaluationLogs = Collections.singletonList(TestHelperUtils.createEvaluationLog());
        PageDto<EvaluationLogDto> expected =
                PageDto.of(evaluationLogs.stream().map(evaluationLogMapper::map).collect(Collectors.toList()), PAGE,
                        TOTAL_ELEMENTS);
        when(evaluationLogPage.getContent()).thenReturn(evaluationLogs);
        when(evaluationLogService.getNextPage(any(PageRequestDto.class))).thenReturn(evaluationLogPage);
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_PARAM, String.valueOf(PAGE))
                .param(SIZE_PARAM, String.valueOf(SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    public void testEvaluationRequestStatusesStatisticsUnauthorized() throws Exception {
        mockMvc.perform(get(REQUEST_STATUS_STATISTICS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testEvaluationRequestStatusesStatisticsOk() throws Exception {
        Map<RequestStatus, Long> requestStatusMap = buildRequestStatusStatisticsMap();
        RequestStatusStatisticsDto requestStatusStatisticsDto = Utils.toRequestStatusesStatistics(requestStatusMap);
        when(evaluationLogService.getRequestStatusesStatistics()).thenReturn(requestStatusMap);
        mockMvc.perform(get(REQUEST_STATUS_STATISTICS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(requestStatusStatisticsDto)));
    }
}
