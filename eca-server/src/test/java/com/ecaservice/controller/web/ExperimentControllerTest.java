package com.ecaservice.controller.web;

import com.ecaservice.TestHelperUtils;
import com.ecaservice.dto.ExperimentRequest;
import com.ecaservice.exception.experiment.ExperimentException;
import com.ecaservice.mapping.ExperimentMapper;
import com.ecaservice.mapping.ExperimentMapperImpl;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.entity.ExperimentResultsEntity;
import com.ecaservice.model.entity.RequestStatus;
import com.ecaservice.model.experiment.ExperimentType;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.repository.ExperimentResultsEntityRepository;
import com.ecaservice.service.UserService;
import com.ecaservice.service.experiment.ExperimentRequestService;
import com.ecaservice.service.experiment.ExperimentResultsLockService;
import com.ecaservice.service.experiment.ExperimentResultsService;
import com.ecaservice.service.experiment.ExperimentService;
import com.ecaservice.user.model.UserDetailsImpl;
import com.ecaservice.util.Utils;
import com.ecaservice.web.dto.model.ChartDataDto;
import com.ecaservice.web.dto.model.CreateExperimentResultDto;
import com.ecaservice.web.dto.model.EvaluationResultsStatus;
import com.ecaservice.web.dto.model.ExperimentDto;
import com.ecaservice.web.dto.model.ExperimentErsReportDto;
import com.ecaservice.web.dto.model.ExperimentResultsDetailsDto;
import com.ecaservice.web.dto.model.PageDto;
import com.ecaservice.web.dto.model.PageRequestDto;
import com.ecaservice.web.dto.model.RequestStatusStatisticsDto;
import com.ecaservice.web.dto.model.SendingStatus;
import eca.core.evaluation.EvaluationMethod;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.MimeTypeUtils;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ecaservice.PageRequestUtils.PAGE_NUMBER;
import static com.ecaservice.PageRequestUtils.PAGE_NUMBER_PARAM;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE;
import static com.ecaservice.PageRequestUtils.PAGE_SIZE_PARAM;
import static com.ecaservice.PageRequestUtils.TOTAL_ELEMENTS;
import static com.ecaservice.TestHelperUtils.TEST_UUID;
import static com.ecaservice.TestHelperUtils.bearerHeader;
import static com.ecaservice.TestHelperUtils.buildRequestStatusStatisticsMap;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for checking {@link ExperimentController} functionality.
 *
 * @author Roman Batygin
 */
@WebMvcTest(controllers = ExperimentController.class)
@Import(ExperimentMapperImpl.class)
public class ExperimentControllerTest extends PageRequestControllerTest {

    private static final String BASE_URL = "/experiment";
    private static final String DETAILS_URL = BASE_URL + "/details/{requestId}";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String DOWNLOAD_TRAINING_DATA_URL = BASE_URL + "/training-data/{requestId}";
    private static final String DOWNLOAD_EXPERIMENT_RESULTS_URL = BASE_URL + "/results/{requestId}";
    private static final String CREATE_EXPERIMENT_URL = BASE_URL + "/create";
    private static final String EXPERIMENT_RESULTS_DETAILS_URL = BASE_URL + "/results/details/{id}";
    private static final String ERS_REPORT_URL = BASE_URL + "/ers-report/{requestId}";
    private static final String EXPERIMENT_RESULTS_SENDING_STATUS_URL =
            BASE_URL + "/ers-report/sending-status/{requestId}";
    private static final String SENT_EVALUATION_RESULTS_URL = BASE_URL + "/sent-evaluation-results";
    private static final String REQUEST_STATUS_STATISTICS_URL = BASE_URL + "/request-statuses-statistics";
    private static final String EXPERIMENT_TYPES_STATISTICS_URL = BASE_URL + "/statistics";

    private static final String EXPERIMENT_TYPE_PARAM = "experimentType";
    private static final String EVALUATION_METHOD_PARAM = "evaluationMethod";
    private static final String TRAINING_DATA_PARAM = "trainingData";
    private static final String ERROR_MESSAGE = "Error";
    private static final long EXPERIMENT_RESULTS_ID = 1L;
    private static final long RESULTS_COUNT = 1L;
    
    @MockBean
    private ExperimentService experimentService;
    @MockBean
    private ExperimentRequestService experimentRequestService;
    @MockBean
    private ApplicationEventPublisher applicationEventPublisher;
    @MockBean
    private ExperimentResultsLockService lockService;
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

    private final MockMultipartFile trainingData =
            new MockMultipartFile(TRAINING_DATA_PARAM, "iris.txt",
                    MimeTypeUtils.TEXT_PLAIN.toString(), "file-content".getBytes(StandardCharsets.UTF_8));
    
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
    public void testGetExperimentDetailsNotFound() throws Exception {
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetExperimentDetailsOk() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(experiment);
        ExperimentDto experimentDto = experimentMapper.map(experiment);
        mockMvc.perform(get(DETAILS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
        expected.setRequestId(experiment.getRequestId());
        when(userService.getCurrentUser()).thenReturn(
                new UserDetailsImpl(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
                        Collections.emptyList()));
        when(experimentRequestService.createExperimentRequest(any(ExperimentRequest.class))).thenReturn(experiment);
        mockMvc.perform(multipart(CREATE_EXPERIMENT_URL)
                .file(trainingData)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(EXPERIMENT_TYPE_PARAM, ExperimentType.NEURAL_NETWORKS.name())
                .param(EVALUATION_METHOD_PARAM, EvaluationMethod.CROSS_VALIDATION.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(EXPERIMENT_TYPE_PARAM, ExperimentType.NEURAL_NETWORKS.name())
                .param(EVALUATION_METHOD_PARAM, EvaluationMethod.CROSS_VALIDATION.name()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    public void testGetExperimentsUnauthorized() throws Exception {
        testGetPageUnauthorized(LIST_URL, Collections.emptyMap());
    }

    @Test
    public void testGetExperimentsWithNullPageNumber() throws Exception {
        testGetPageWithNullPageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    public void testGetExperimentsWithNullPageSize() throws Exception {
        testGetPageWithNullPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    public void testGetExperimentsWithZeroPageSize() throws Exception {
        testGetPageWithZeroPageSize(LIST_URL, Collections.emptyMap());
    }

    @Test
    public void testGetExperimentsWithNegativePageNumber() throws Exception {
        testGetPageWithNegativePageNumber(LIST_URL, Collections.emptyMap());
    }

    @Test
    public void testGetExperimentsWithEmptyFilterRequestName() throws Exception {
        testGetPageWithEmptyFilterRequestName(LIST_URL, Collections.emptyMap());
    }

    @Test
    public void testGetExperimentsWithNullMatchMode() throws Exception {
        testGetPageWithNullMatchMode(LIST_URL, Collections.emptyMap());
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
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .param(PAGE_NUMBER_PARAM, String.valueOf(PAGE_NUMBER))
                .param(PAGE_SIZE_PARAM, String.valueOf(PAGE_SIZE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(requestStatusStatisticsDto)));
    }

    @Test
    public void testGetExperimentResultsDetailsUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_RESULTS_DETAILS_URL, EXPERIMENT_RESULTS_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperimentResultsDetailsNotFound() throws Exception {
        when(experimentResultsEntityRepository.findById(EXPERIMENT_RESULTS_ID)).thenReturn(Optional.empty());
        mockMvc.perform(get(EXPERIMENT_RESULTS_DETAILS_URL, EXPERIMENT_RESULTS_ID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isNotFound());
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
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(experimentResultsDetailsDto)));
    }

    @Test
    public void testGetErsReportUnauthorized() throws Exception {
        mockMvc.perform(get(ERS_REPORT_URL, TEST_UUID)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetErsReportForNotExistingExperiment() throws Exception {
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get(ERS_REPORT_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetErsReportOk() throws Exception {
        ExperimentErsReportDto expected = new ExperimentErsReportDto();
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(new Experiment());
        when(experimentResultsService.getErsReport(any(Experiment.class))).thenReturn(expected);
        mockMvc.perform(get(ERS_REPORT_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    public void testSentEvaluationResultsUnauthorized() throws Exception {
        mockMvc.perform(post(SENT_EVALUATION_RESULTS_URL)
                .content(TEST_UUID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testSentEvaluationResultsForNotExistingExperiment() throws Exception {
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(null);
        mockMvc.perform(post(SENT_EVALUATION_RESULTS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(TEST_UUID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSentEvaluationResultsForNotFinishedExperiment() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        experiment.setRequestStatus(RequestStatus.ERROR);
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(experiment);
        mockMvc.perform(post(SENT_EVALUATION_RESULTS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(TEST_UUID))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSentEmptyEvaluationResults() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(experiment);
        when(experimentResultsEntityRepository.countByExperiment(experiment)).thenReturn(0L);
        mockMvc.perform(post(SENT_EVALUATION_RESULTS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(TEST_UUID))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAlreadySentEvaluationResults() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(experiment);
        when(experimentResultsEntityRepository.countByExperiment(experiment)).thenReturn(RESULTS_COUNT);
        when(experimentResultsEntityRepository.getSentResultsCount(experiment)).thenReturn(RESULTS_COUNT);
        mockMvc.perform(post(SENT_EVALUATION_RESULTS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(TEST_UUID))
                .andExpect(status().isOk());
    }

    @Test
    public void testSentEvaluationResultsForDeletedExperiment() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        experiment.setDeletedDate(LocalDateTime.now());
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(experiment);
        when(experimentResultsEntityRepository.countByExperiment(experiment)).thenReturn(RESULTS_COUNT + 1);
        when(experimentResultsEntityRepository.getSentResultsCount(experiment)).thenReturn(RESULTS_COUNT);
        mockMvc.perform(post(SENT_EVALUATION_RESULTS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(TEST_UUID))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testSentEvaluationResultsWithConflict() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(experiment);
        when(experimentResultsEntityRepository.countByExperiment(experiment)).thenReturn(RESULTS_COUNT + 1);
        when(experimentResultsEntityRepository.getSentResultsCount(experiment)).thenReturn(RESULTS_COUNT);
        when(lockService.locked(experiment.getRequestId())).thenReturn(true);
        mockMvc.perform(post(SENT_EVALUATION_RESULTS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(TEST_UUID))
                .andExpect(status().isConflict());
    }

    @Test
    public void testSentEvaluationResultsOk() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString(), RequestStatus.FINISHED);
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(experiment);
        when(experimentResultsEntityRepository.countByExperiment(experiment)).thenReturn(RESULTS_COUNT + 1);
        when(experimentResultsEntityRepository.getSentResultsCount(experiment)).thenReturn(RESULTS_COUNT);
        when(lockService.locked(experiment.getRequestId())).thenReturn(false);
        mockMvc.perform(post(SENT_EVALUATION_RESULTS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken()))
                .content(TEST_UUID))
                .andExpect(status().isOk());
        verify(lockService, atLeastOnce()).lock(experiment.getRequestId());
    }

    @Test
    public void testGetExperimentResultsSendingStatusUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_RESULTS_SENDING_STATUS_URL, TEST_UUID)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperimentResultsSendingStatusNotFound() throws Exception {
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get(EXPERIMENT_RESULTS_SENDING_STATUS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetExperimentResultsSendingStatusOk() throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(TEST_UUID);
        SendingStatus sendingStatus = new SendingStatus(experiment.getRequestId(), true);
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(experiment);
        when(lockService.locked(experiment.getRequestId())).thenReturn(true);
        mockMvc.perform(get(EXPERIMENT_RESULTS_SENDING_STATUS_URL, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(sendingStatus)));
    }

    @Test
    public void testGetExperimentTypesStatisticsUnauthorized() throws Exception {
        mockMvc.perform(get(EXPERIMENT_TYPES_STATISTICS_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetExperimentTypesStatisticsOk() throws Exception {
        Map<ExperimentType, Long> experimentTypesMap = TestHelperUtils.buildExperimentTypeStatisticMap();
        when(experimentService.getExperimentTypesStatistics(null, null)).thenReturn(experimentTypesMap);
        List<ChartDataDto> chartDataDtoList = experimentTypesMap.entrySet().stream().map(
                entry -> new ChartDataDto(entry.getKey().name(), entry.getKey().getDescription(),
                        entry.getValue())).collect(Collectors.toList());
        mockMvc.perform(get(EXPERIMENT_TYPES_STATISTICS_URL)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(chartDataDtoList)));

    }

    private void testDownloadFileForNotExistingExperiment(String url) throws Exception {
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(null);
        mockMvc.perform(get(url, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isNotFound());
    }

    private void testDownloadNotExistingExperimentFile(String url) throws Exception {
        Experiment experiment = TestHelperUtils.createExperiment(UUID.randomUUID().toString());
        when(experimentRepository.findByRequestId(TEST_UUID)).thenReturn(experiment);
        mockMvc.perform(get(url, TEST_UUID)
                .header(HttpHeaders.AUTHORIZATION, bearerHeader(getAccessToken())))
                .andExpect(status().isNotFound());
    }
}