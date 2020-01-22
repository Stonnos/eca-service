package com.ecaservice.controller.web;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.configuation.annotation.Oauth2TestConfiguration;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.exception.experiment.ExperimentException;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.model.entity.EvaluationLog_;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.UserService;
import com.ecaservice.service.ers.ErsService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentResultsService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.token.TokenService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.MatchMode;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.EvaluationMethod;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ExperimentController} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest(Utils.class)
@WebMvcTest(controllers = ExperimentController.class)
@Oauth2TestConfiguration
@Import(ExperimentMapperImpl.class)
public class ExperimentControllerTest {

    private static final String BASE_URL = "/experiment";
    private static final String DETAILS_URL = BASE_URL + "/details/{uuid}";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String DOWNLOAD_TRAINING_DATA_URL = BASE_URL + "/training-data/{uuid}";
    private static final String DOWNLOAD_EXPERIMENT_RESULTS_URL = BASE_URL + "/results/{uuid}";
    private static final String CREATE_EXPERIMENT_URL = BASE_URL + "/create";
    private static final String EXPERIMENT_RESULTS_DETAILS_URL = BASE_URL + "/results/details/{id}";
    private static final String ERS_REPORT_URL = BASE_URL + "/ers-report/{uuid}";
    private static final String REQUEST_STATUS_STATISTICS_URL = BASE_URL + "/request-statuses-statistics";

    private static final String EXPERIMENT_TYPE_PARAM = "experimentType";
    private static final String EVALUATION_METHOD_PARAM = "evaluationMethod";
    private static final String TRAINING_DATA_PARAM = "trainingData";
    private static final String ERROR_MESSAGE = "Error";
    private static final long EXPERIMENT_RESULTS_ID = 1L;

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

    private final MockMultipartFile trainingData =
            new MockMultipartFile(TRAINING_DATA_PARAM, "iris.txt", "text/plain", "file-content".getBytes());

    @Before
    public void init() throws Exception {
        accessToken = tokenService.obtainAccessToken();
    }

    @Test
    public void testDownloadTrainingDataUnauthorized() throws Exception {
        mockMvc.perform(get(DOWNLOAD_TRAINING_DATA_URL, TEST_UUID)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testDownloadTrainingDataForNotExistingExperiment() throws Exception {
        testDownloadFileForNotExistingExperiment(DOWNLOAD_TRAINING_DATA_URL);
    }

    @Test
    public void testDownloadNotExistingTrainingDataFile() throws Exception {
        testDownloadNotExistingExperimentFile(DOWNLOAD_TRAINING_DATA_URL);
    }

    @Test
    public void testDownloadExperimentResultsUnauthorized() throws Exception {
        mockMvc.perform(get(DOWNLOAD_EXPERIMENT_RESULTS_URL, TEST_UUID)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testDownloadExperimentResultsForNotExistingExperiment() throws Exception {
        testDownloadFileForNotExistingExperiment(DOWNLOAD_EXPERIMENT_RESULTS_URL);
    }

    @Test
    public void testDownloadExperimentResultsFile() throws Exception {
        testDownloadNotExistingExperimentFile(DOWNLOAD_EXPERIMENT_RESULTS_URL);
    }

    @Test
    public void testGetExperimentDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)).andExpect(status().isUnauthorized());
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
    public void testCreateExperimentUnauthorized() throws Exception {
        mockMvc.perform(multipart(CREATE_EXPERIMENT_URL)
                .file(trainingData)
                .param(EXPERIMENT_TYPE_PARAM, ExperimentType.NEURAL_NETWORKS.name())
                .param(EVALUATION_METHOD_PARAM, EvaluationMethod.CROSS_VALIDATION.name()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateExperimentSuccess() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        CreateExperimentResultDto expected = new CreateExperimentResultDto();
        expected.setCreated(true);
        expected.setUuid(experiment.getUuid());
        when(userService.getCurrentUser()).thenReturn(
                new UserDetailsImpl(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
                        Collections.emptyList()));
        when(experimentRequestService.createExperimentRequest(any(ExperimentRequest.class))).thenReturn(experiment);
        mockMvc.perform(multipart(CREATE_EXPERIMENT_URL)
                .file(trainingData)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(EXPERIMENT_TYPE_PARAM, ExperimentType.NEURAL_NETWORKS.name())
                .param(EVALUATION_METHOD_PARAM, EvaluationMethod.CROSS_VALIDATION.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    public void testCreateExperimentWithError() throws Exception {
        CreateExperimentResultDto expected = new CreateExperimentResultDto();
        expected.setErrorMessage(ERROR_MESSAGE);
        when(userService.getCurrentUser()).thenReturn(
                new UserDetailsImpl(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
                        Collections.emptyList()));
        when(experimentRequestService.createExperimentRequest(any(ExperimentRequest.class))).thenThrow(
                new ExperimentException(ERROR_MESSAGE));
        mockMvc.perform(multipart(CREATE_EXPERIMENT_URL)
                .file(trainingData)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken))
                .param(EXPERIMENT_TYPE_PARAM, ExperimentType.NEURAL_NETWORKS.name())
                .param(EVALUATION_METHOD_PARAM, EvaluationMethod.CROSS_VALIDATION.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
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

    @Test
    public void testGetExperimentResultsDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_RESULTS_DETAILS_URL, EXPERIMENT_RESULTS_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperimentResultsDetailsBadRequest() throws Exception {
        when(experimentResultsEntityRepository.findById(EXPERIMENT_RESULTS_ID)).thenReturn(Optional.empty());
        mockMvc.perform(get(EXPERIMENT_RESULTS_DETAILS_URL, EXPERIMENT_RESULTS_ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetExperimentResultsDetailsOk() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        ExperimentResultsEntity experimentResultsEntity = TestHelperUtils.createExperimentResultsEntity(experiment);
        ExperimentResultsDetailsDto experimentResultsDetailsDto = new ExperimentResultsDetailsDto();
        experimentResultsDetailsDto.setExperimentDto(experimentMapper.map(experiment));
        experimentResultsDetailsDto.setEvaluationResultsDto(
                TestHelperUtils.createEvaluationResultsDto(EvaluationResultsStatus.RESULTS_RECEIVED));
        when(experimentResultsEntityRepository.findById(EXPERIMENT_RESULTS_ID)).thenReturn(
                Optional.of(experimentResultsEntity));
        when(experimentResultsService.getExperimentResultsDetails(experimentResultsEntity)).thenReturn(
                experimentResultsDetailsDto);
        mockMvc.perform(get(EXPERIMENT_RESULTS_DETAILS_URL, EXPERIMENT_RESULTS_ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(experimentResultsDetailsDto)));
    }

    @Test
    public void testGetErsReportUnauthorized() throws Exception {
        mockMvc.perform(get(ERS_REPORT_URL, TEST_UUID)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetErsReportForNotExistingExperiment() throws Exception {
        when(experimentRepository.findByUuid(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get(ERS_REPORT_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetErsReportOk() throws Exception {
        ExperimentErsReportDto expected = new ExperimentErsReportDto();
        when(experimentRepository.findByUuid(TEST_UUID)).thenReturn(new Experiment());
        when(experimentResultsService.getErsReport(any(Experiment.class))).thenReturn(expected);
        mockMvc.perform(get(ERS_REPORT_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    private void testDownloadFileForNotExistingExperiment(String url) throws Exception {
        when(experimentRepository.findByUuid(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get(url, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }

    private void testDownloadNotExistingExperimentFile(String url) throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(experimentRepository.findByUuid(TEST_UUID)).thenReturn(experiment);
        PowerMockito.mockStatic(Utils.class);
        when(Utils.existsFile(any(File.class))).thenReturn(false);
        mockMvc.perform(get(url, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(accessToken)))
                .andExpect(status().isBadRequest());
    }
}
