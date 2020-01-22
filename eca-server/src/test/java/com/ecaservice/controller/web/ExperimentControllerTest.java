package com.ecaservice.controller.web;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.configuation.annotation.Oauth2TestConfiguration;
import com.ecaservice.mapping.ClassifierInfoMapperImpl;
import com.ecaservice.mapping.ClassifierInputOptionsMapperImpl;
import com.ecaservice.mapping.EvaluationLogMapper;
import com.ecaservice.mapping.EvaluationLogMapperImpl;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.mapping.InstancesInfoMapperImpl;
import com.ecaservice.model.entity.EvaluationLog;
import com.ecaservice.model.entity.EvaluationLog_;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.repository.EvaluationLogRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.UserService;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.evaluation.EvaluationLogService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentResultsService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.token.TokenService;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.EvaluationLogDetailsDto;
import com.ecaservice.web.dto.model.EvaluationLogDto;
import com.ecaservice.web.dto.model.ExperimentDto;
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
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecaservice.PageRequestUtils.FILTER_MATCH_MODE_PARAM;
import static com.ecaservice.PageRequestUtils.FILTER_NAME_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE_PARAM;
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
 * Unit tests for checking {@link ExperimentController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ExperimentController.class)
@Oauth2TestConfiguration
@Import(ExperimentMapperImpl.class)
public class ExperimentControllerTest {

    private static final String BASE_URL = "/experiment";
    private static final String DETAILS_URL = BASE_URL + "/details/{uuid}";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String REQUEST_STATUS_STATISTICS_URL = BASE_URL + "/request-statuses-statistics";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private ExperimentService experimentService;
    @MockBean
    private ExperimentRequestService experimentRequestService;
    @MockBean
    private ErsService ersService;
    @MockBean
    private ExperimentResultsService experimentResultsService;
    @MockBean
    private UserService userService;
    @MockBean
    private ExperimentRepository experimentRepository;
    @MockBean
    private ExperimentResultsEntityRepository experimentResultsEntityRepository;

    @Inject
    private ExperimentMapper experimentMapper;
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
    public void testGetExperimentDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAILS_URL, TEST_UUID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperimentDetailsBadRequest() throws Exception {
        when(experimentRepository.findByUuid(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetExperimentDetailsOk() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(experimentRepository.findByUuid(TEST_UUID)).thenReturn(experiment);
        ExperimentDto experimentDto = experimentMapper.map(experiment);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(experimentDto)));
    }

    @Test
    public void testGetExperimentsUnauthorized() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperimentsWithNullPageNumber() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetExperimentsWithNullPageSize() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetExperimentsWithEmptyFilterRequestName() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, StringUtils.EMPTY)
                .param(FILTER_MATCH_MODE_PARAM, MatchMode.RANGE.name()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetExperimentsWithNullMatchMode() throws Exception {
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE))
                .param(FILTER_NAME_PARAM, EvaluationLog_.CREATION_DATE))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetExperimentsLogsOk() throws Exception {
        Page<Experiment> experimentPage = Mockito.mock(Page.class);
        when(experimentPage.getTotalElements()).thenReturn(TOTAL_ELEMENTS);
        List<Experiment> experiments =
                Collections.singletonList(TestHelperUtils.createExperiment(UUID.randomUUID().toString()));
        PageDto<ExperimentDto> expected = PageDto.of(experimentMapper.map(experiments), PAGE_NUMBER, TOTAL_ELEMENTS);
        when(experimentPage.getContent()).thenReturn(experiments);
        when(experimentService.getNextPage(any(PageRequestDto.class))).thenReturn(experimentPage);
        mockMvc.perform(get(LIST_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    public void testExperimentsRequestStatusesStatisticsUnauthorized() throws Exception {
        mockMvc.perform(get(REQUEST_STATUS_STATISTICS_URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testExperimentsRequestStatusesStatisticsOk() throws Exception {
        Map<RequestStatus, Long> requestStatusMap = buildRequestStatusStatisticsMap();
        RequestStatusStatisticsDto requestStatusStatisticsDto = Utils.toRequestStatusesStatistics(requestStatusMap);
        when(experimentService.getRequestStatusesStatistics()).thenReturn(requestStatusMap);
        mockMvc.perform(get(REQUEST_STATUS_STATISTICS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(requestStatusStatisticsDto)));
    }
}
